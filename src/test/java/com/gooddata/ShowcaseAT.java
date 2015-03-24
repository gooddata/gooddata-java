package com.gooddata;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.ProcessExecution;
import com.gooddata.dataload.processes.ProcessExecutionDetail;
import com.gooddata.dataload.processes.ProcessService;
import com.gooddata.dataload.processes.ProcessType;
import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.md.Attribute;
import com.gooddata.md.Fact;
import com.gooddata.md.MetadataService;
import com.gooddata.md.Metric;
import com.gooddata.md.report.AttributeInGrid;
import com.gooddata.md.report.GridElement;
import com.gooddata.md.report.GridReportDefinitionContent;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.model.ModelDiff;
import com.gooddata.model.ModelService;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectService;
import com.gooddata.report.ReportExportFormat;
import com.gooddata.report.ReportService;
import com.gooddata.warehouse.Warehouse;
import com.gooddata.warehouse.WarehouseService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.springframework.core.io.ClassPathResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.UUID;

import static com.gooddata.ProcessIdMatcher.hasSameIdAs;
import static com.gooddata.ProjectIdMatcher.hasSameIdAs;
import static com.gooddata.WarehouseIdMatcher.hasSameIdAs;
import static com.gooddata.md.Restriction.identifier;
import static java.nio.file.Files.createTempDirectory;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class ShowcaseAT {

    private String title;
    private GoodData gd;
    private Project project;
    private String fact;
    private Attribute attr;
    private Metric metric;
    private ReportDefinition reportDefinition;
    private String file;
    private String projectToken;
    private String warehouseToken;
    private Warehouse warehouse;
    private DataloadProcess process;

    @BeforeClass
    public void setUp() throws Exception {
        projectToken = getProperty("projectToken");
        warehouseToken = getProperty("warehouseToken");
        gd = new GoodData(getProperty("host"), getProperty("login"), getProperty("pass"));
        title = "sdktest " + new LocalDate() + " " + System.getenv("BUILD_NUMBER");
    }

    private static String getProperty(String name) {
        final String value = System.getenv(name);
        if (value != null) {
            return value;
        }
        throw new IllegalArgumentException("Environment variable " + name + " not found! Available variables: " +
                System.getenv().keySet());
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (gd != null && project != null) {
            gd.getProjectService().removeProject(project);
        }
        if (gd != null) {
            gd.logout();
        }
    }

    @Test
    public void login() throws Exception {
        final AccountService accountService = gd.getAccountService();
        final Account current = accountService.getCurrent();
        assertThat(current.getId(), is(notNullValue()));
    }

    @Test(groups = "project", dependsOnMethods = "login")
    public void createProject() throws Exception {
        project = gd.getProjectService().createProject(new Project(title, projectToken)).get();
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjects() throws Exception {
        final ProjectService projectService = gd.getProjectService();

        final Collection<Project> projects = projectService.getProjects();
        assertThat(projects, hasItem(hasSameIdAs(project)));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectById() throws Exception {
        final Project project = gd.getProjectService().getProjectById(this.project.getId());
        assertThat(project, hasSameIdAs(this.project));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void getProjectByUri() throws Exception {
        final Project project = gd.getProjectService().getProjectByUri(this.project.getUri());
        assertThat(project, hasSameIdAs(this.project));
    }

    @Test(groups = "model", dependsOnGroups = "project")
    public void createModel() throws Exception {
        final ModelService modelService = gd.getModelService();

        final ModelDiff projectModelDiff = modelService.getProjectModelDiff(project,
                new InputStreamReader(getClass().getResourceAsStream("/person.json"))).get();
        modelService.updateProjectModel(project, projectModelDiff).get();
    }

    @Test(groups = "md", dependsOnGroups = "model")
    public void getObjs() throws Exception {
        final MetadataService md = gd.getMetadataService();

        fact = md.getObjUri(project, Fact.class, identifier("fact.person.shoesize"));
        attr = md.getObj(project, Attribute.class, identifier("attr.person.department"));
    }

    @Test(groups = "md", dependsOnMethods = "getObjs")
    public void createMetric() throws Exception {
        final MetadataService md = gd.getMetadataService();
        metric = md.createObj(project, new Metric("Avg shoe size", "SELECT AVG([" + fact + "])", "#,##0"));
    }

    @Test(groups = "md", dependsOnMethods = "createMetric")
    public void createReport() throws Exception {
        final MetadataService md = gd.getMetadataService();

        reportDefinition = md.createObj(project, GridReportDefinitionContent.create(
                "Department avg shoe size",
                asList("metricGroup"),
                asList(new AttributeInGrid(attr.getDefaultDisplayForm().getUri())),
                asList(new GridElement(metric.getUri(), "Avg shoe size"))
        ));
        md.createObj(project, new Report(reportDefinition.getTitle(), reportDefinition));
    }

    @Test(groups = "datastore", dependsOnMethods = "login")
    public void datastoreUpload() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();

        file = "/" + UUID.randomUUID().toString() + "/file.csv";
        dataStoreService.upload(file, getClass().getResourceAsStream("/person.csv"));
    }

    @Test(groups = "datastore", dependsOnMethods = "datastoreUpload")
    public void datastoreDownload() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();

        final File file = File.createTempFile("file", ".txt");
        try (InputStream stream = dataStoreService.download(this.file)) {
            file.deleteOnExit();
            IOUtils.copy(stream, new FileOutputStream(file));
        } finally {
            file.delete();
        }
    }

    @Test(groups = "datastore", dependsOnMethods = "datastoreDownload")
    public void datastoreDelete() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();
        dataStoreService.delete(this.file);
    }

    @Test(groups = "dataset", dependsOnGroups = {"md", "datastore"})
    public void loadDataset() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest manifest = datasetService.getDatasetManifest(project, "dataset.person");
        datasetService.loadDataset(project, manifest, getClass().getResourceAsStream("/person.csv")).get();
    }

    @Test(groups = "dataset", dependsOnMethods = "loadDataset")
    public void updateData() {
        final DatasetService datasetService = gd.getDatasetService();
        datasetService.updateProjectData(project, "DELETE FROM {attr.person.name} WHERE {label.person.name} = \"not exists\";");
    }

    @Test(groups = "report", dependsOnGroups = "dataset")
    public void exportReportDefinition() throws Exception {
        final ReportService reportService = gd.getReportService();
        reportService.exportReport(reportDefinition, ReportExportFormat.CSV, System.out);
    }

    @Test(groups = "warehouse", dependsOnMethods = "login")
    public void createWarehouse() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        warehouse = warehouseService.createWarehouse(new Warehouse(title, warehouseToken)).get();
        String jdbc = warehouse.getJdbcConnectionString();
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void getWarehouse() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        final Warehouse warehouse = warehouseService.getWarehouseById(this.warehouse.getId());
        assertThat(warehouse, is(hasSameIdAs(warehouse)));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void listWarehouses() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        final Collection<Warehouse> warehouses = warehouseService.listWarehouses();
        assertThat(warehouses, hasItem(hasSameIdAs(warehouse)));
    }

    @Test(dependsOnGroups = "warehouse")
    public void removeWarehouse() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        warehouseService.removeWarehouse(warehouse);
    }

    @Test(groups = "process", dependsOnGroups = "project")
    public void createProcess() throws Exception {
        final File dir = createTempDirectory("sdktest").toFile();
        try {
            copy("sdktest.grf", dir);
            copy("workspace.prm", dir);
            process = gd.getProcessService().createProcess(project, new DataloadProcess(title, ProcessType.GRAPH), dir);
        } finally {
            FileUtils.deleteDirectory(dir);
        }
    }

    public void copy(final String file, final File dir) throws IOException {
        IOUtils.copy(
                new ClassPathResource("/dataload/processes/testgraph/" + file, getClass()).getInputStream(),
                new FileOutputStream(new File(dir, file))
        );
    }

    @Test(groups = "process", dependsOnMethods = "createProcess")
    public void processes() {
        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, hasItem(hasSameIdAs(process)));
    }

    @Test(groups = "process", dependsOnMethods = "createProcess")
    public void executeProcess() throws Exception {
        ProcessService processService = gd.getProcessService();
        ProcessExecutionDetail executionDetail = processService.executeProcess(new ProcessExecution(process, "sdktest.grf")).get();
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        processService.getExecutionLog(executionDetail, outputStream);
        assertThat(outputStream.toString("UTF-8"), allOf(containsString("infoooooooo"), containsString("waaaaaaaarn"),
                containsString("errooooooor"), containsString("fataaaaaaal")));
    }

    @Test(dependsOnGroups = "process")
    public void removeProcess() throws Exception {
        gd.getProcessService().removeProcess(process);
        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, not(hasItem(hasSameIdAs(process))));
    }
}
