package com.gooddata;

import com.gooddata.account.Account;
import com.gooddata.account.AccountService;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import com.gooddata.dataload.processes.DataloadProcess;
import com.gooddata.dataload.processes.ProcessExecution;
import com.gooddata.dataload.processes.ProcessExecutionDetail;
import com.gooddata.dataload.processes.ProcessExecutionException;
import com.gooddata.dataload.processes.ProcessService;
import com.gooddata.dataload.processes.ProcessType;
import com.gooddata.dataload.processes.Schedule;
import com.gooddata.dataload.processes.ScheduleState;
import com.gooddata.dataset.DatasetManifest;
import com.gooddata.dataset.DatasetService;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.md.*;
import com.gooddata.md.ScheduledMail;
import com.gooddata.md.report.AttributeInGrid;
import com.gooddata.md.report.Filter;
import com.gooddata.md.report.GridElement;
import com.gooddata.md.report.GridReportDefinitionContent;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.model.ModelDiff;
import com.gooddata.model.ModelService;
import com.gooddata.project.Project;
import com.gooddata.project.ProjectFeatureFlag;
import com.gooddata.project.ProjectService;
import com.gooddata.project.ProjectValidationResults;
import com.gooddata.project.Role;
import com.gooddata.project.User;
import com.gooddata.report.ReportExportFormat;
import com.gooddata.report.ReportService;
import com.gooddata.warehouse.Warehouse;
import com.gooddata.warehouse.WarehouseService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.gooddata.ProcessIdMatcher.hasSameIdAs;
import static com.gooddata.ProjectIdMatcher.hasSameIdAs;
import static com.gooddata.WarehouseIdMatcher.hasSameIdAs;
import static com.gooddata.md.Restriction.identifier;
import static java.nio.file.Files.createTempDirectory;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.testng.AssertJUnit.fail;
import static com.gooddata.report.ReportExportFormat.*;

public class ShowcaseAT {

    private static final String PROJECT_FEATURE_FLAG = "testFeatureFlag";

    private String title;
    private GoodData gd;
    private Project project;
    private String fact;
    private Attribute attr;
    private Metric metric;
    private ReportDefinition reportDefinition;
    private Report report;
    private String file;
    private String projectToken;
    private String warehouseToken;
    private Warehouse warehouse;
    private DataloadProcess process;
    private ScheduledMail scheduledMail;

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
    public void listProjectUsers() throws Exception {
        final ProjectService projectService = gd.getProjectService();

        final List<User> users = new ArrayList<>();
        List<User> page;
        while (!(page = projectService.listUsers(project, new PageRequest(users.size(), 100))).isEmpty()) {
            users.addAll(page);
        }
        assertThat(users, not(empty()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void listProjectRoles() throws Exception {
        final ProjectService projectService = gd.getProjectService();

        final Set<Role> roles = projectService.getRoles(project);
        assertThat(roles, not(empty()));
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

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void validateProject() throws Exception {
        final ProjectValidationResults results = gd.getProjectService().validateProject(project).get();
        assertThat(results, is(notNullValue()));
        assertThat(results.getResults(), is(notNullValue()));
    }

    @Test(groups = "project", dependsOnMethods = "createProject")
    public void createProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().createFeatureFlag(project, new ProjectFeatureFlag(PROJECT_FEATURE_FLAG));
        checkFeatureFlag(featureFlag, true);
    }

    @Test(groups = "project", dependsOnMethods = "createProjectFeatureFlag")
    public void listProjectFeatureFlags() throws Exception {
        gd.getProjectService().createFeatureFlag(project, new ProjectFeatureFlag("mostRecentFeatureFlag"));

        final List<ProjectFeatureFlag> projectFeatureFlags = gd.getProjectService().listFeatureFlags(project);

        assertThat(projectFeatureFlags, containsInAnyOrder(
                new ProjectFeatureFlag("mostRecentFeatureFlag", true),
                new ProjectFeatureFlag(PROJECT_FEATURE_FLAG, true)));
    }

    @Test(groups = "project", dependsOnMethods = "createProjectFeatureFlag")
    public void getProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().getFeatureFlag(project, PROJECT_FEATURE_FLAG);
        checkFeatureFlag(featureFlag, true);
    }

    @Test(groups = "project", dependsOnMethods = "getProjectFeatureFlag")
    public void updateProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().getFeatureFlag(project, PROJECT_FEATURE_FLAG);

        // disable (update) feature flag
        featureFlag.setEnabled(false);
        final ProjectFeatureFlag disabledFlag = gd.getProjectService().updateFeatureFlag(featureFlag);
        checkFeatureFlag(disabledFlag, false);

        // enable again
        featureFlag.setEnabled(true);
        final ProjectFeatureFlag enabledFlag = gd.getProjectService().updateFeatureFlag(featureFlag);
        checkFeatureFlag(enabledFlag, true);
    }

    @Test(groups = "project", dependsOnMethods = "createProjectFeatureFlag")
    public void deleteProjectFeatureFlag() throws Exception {
        final ProjectFeatureFlag featureFlag =
                gd.getProjectService().createFeatureFlag(project, new ProjectFeatureFlag("temporaryFeatureFlag"));

        gd.getProjectService().deleteFeatureFlag(featureFlag);

        try {
            gd.getProjectService().getFeatureFlag(project, featureFlag.getName());
            fail("Feature flag has not been deleted properly. HTTP status NOT FOUND expected.");
        } catch (GoodDataRestException e) {
            assertThat(e.getStatusCode(), is(HttpStatus.NOT_FOUND.value()));
        }
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
                asList(new GridElement(metric.getUri(), "Avg shoe size")),
                asList(new Filter("(SELECT [" + metric.getUri() + "]) >= 0"))
        ));
        report = md.createObj(project, new Report(reportDefinition.getTitle(), reportDefinition));
    }

    @Test(groups = "md", dependsOnMethods = "createReport")
    public void createScheduledMail() throws Exception {
        final MetadataService md = gd.getMetadataService();

        scheduledMail = md.createObj(project,
                (new ScheduledMail("Scheduled Mail Title", "Scheduled Mail Summary"))
                                            .setRecurrency("0:0:0:1*12:0:0")
                                            .setStartDate(new LocalDate(2012, 6, 5))
                                            .setTimeZone("America/Los_Angeles")
                                            .addToAddress(getProperty("login"))
                                            .addBccAddress(getProperty("login"))
                                            .setSubject("Mail subject")
                                            .setBody("Mail body")
                                            .addReportAttachment(reportDefinition,
                                                                 Collections.singletonMap("pageOrientation", "landscape"),
                                                                 PDF, XLS));
    }

    @Test(groups = "md", dependsOnMethods = "createScheduledMail")
    public void retrieveScheduledMail() throws Exception {
        final MetadataService md = gd.getMetadataService();
        Collection<Entry> result = md.find(project, ScheduledMail.class);
        assertThat(result, hasSize(1));
        for (Entry e : result) {
            ScheduledMail schedule = md.getObjByUri(e.getLink(), ScheduledMail.class);
        }
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

    @Test(groups = "dataset", dependsOnMethods = {"loadDataset"})
    public void loadDatasetBatch() throws Exception {
        final DatasetService datasetService = gd.getDatasetService();

        final DatasetManifest personManifest = datasetService.getDatasetManifest(project, "dataset.person");
        personManifest.setSource(getClass().getResourceAsStream("/person.csv"));
        final DatasetManifest cityManifest = datasetService.getDatasetManifest(project, "dataset.city");
        cityManifest.setSource(getClass().getResourceAsStream("/city.csv"));

        datasetService.loadDatasets(project, personManifest, cityManifest).get();
    }

    @Test(groups = "dataset", dependsOnMethods = "loadDatasetBatch")
    public void updateData() {
        final DatasetService datasetService = gd.getDatasetService();
        datasetService.updateProjectData(project, "DELETE FROM {attr.person.name} WHERE {label.person.name} = \"not exists\";");
    }

    @Test(groups = "report", dependsOnGroups = "dataset")
    public void exportReportDefinition() throws Exception {
        final ReportService reportService = gd.getReportService();
        reportService.exportReport(reportDefinition, ReportExportFormat.CSV, System.out);
    }

    @Test(dependsOnGroups = "report")
    public void removeReport() throws Exception {
        final MetadataService metadataService = gd.getMetadataService();
        metadataService.removeObj(report);
    }

    @Test(dependsOnMethods = "removeReport")
    public void removeDefinition() throws Exception {
        final MetadataService metadataService = gd.getMetadataService();
        metadataService.removeObj(reportDefinition);
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
            copy("invalid.grf", dir);
            copy("workspace.prm", dir);
            process = gd.getProcessService().createProcess(project, new DataloadProcess(title, ProcessType.GRAPH), dir);
            final Schedule schedule = gd.getProcessService().createSchedule(project, new Schedule(process, "sdktest.grf", "0 0 * * *"));

            assertThat(schedule, notNullValue());
            assertThat(schedule.getExecutable(), is("sdktest.grf"));

            final PageableList<Schedule> collection = gd.getProcessService().listSchedules(project);
            assertThat(collection, notNullValue());
            assertThat(collection, hasSize(1));
            assertThat(collection.getNextPage(), nullValue());

            schedule.setState(ScheduleState.DISABLED);
            assertThat(gd.getProcessService().updateSchedule(project, schedule).isEnabled(), is(false));

            gd.getProcessService().removeSchedule(schedule);
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

    @Test(groups = "process", dependsOnMethods = "createProcess",
            expectedExceptions = ProcessExecutionException.class, expectedExceptionsMessageRegExp = "(?s)Can't execute.*")
    public void failExecuteProcess() throws Exception {
        ProcessService processService = gd.getProcessService();
        processService.executeProcess(new ProcessExecution(process, "invalid.grf")).get();
    }

    @Test(dependsOnGroups = "process")
    public void removeProcess() throws Exception {
        gd.getProcessService().removeProcess(process);
        final Collection<DataloadProcess> processes = gd.getProcessService().listProcesses(project);
        assertThat(processes, not(hasItem(hasSameIdAs(process))));
    }



    private void checkFeatureFlag(ProjectFeatureFlag featureFlag, boolean expectedValue) {
        assertThat(featureFlag, is(notNullValue()));
        assertThat(featureFlag.getName(), is(PROJECT_FEATURE_FLAG));
        assertThat(featureFlag.getEnabled(), is(expectedValue));
    }

}
