package com.gooddata.dataset;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.GoodDataException;
import com.gooddata.gdc.TaskStatus;
import com.gooddata.project.Project;
import com.gooddata.util.ResourceUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.util.Collection;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.testng.Assert.fail;

public class DatasetServiceIT extends AbstractGoodDataIT {

    private static final String STATUS_URI = "/gdc/md/PROJECT_ID/tasks/TASK_ID/status";

    private static final String DML_MAQL = "DELETE FROM {attr.logs.phase_id} WHERE {created.date.yyyymmdd} < \"2015-01-18\"";

    private Project project;

    @BeforeClass
    public void setUpClass() throws Exception {
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
            .respond()
                .withBody(readFromResource("/gdc/gdc.json"));
        onRequest()
                .havingPath(startsWith("/uploads/"))
                .havingMethodEqualTo("PUT")
            .respond()
                .withStatus(200);
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/etl/pull")
                .havingMethodEqualTo("POST")
            .respond()
                .withStatus(201)
                .withBody(readFromResource("/dataset/pullTask.json"));
    }

    @Test
    public void shouldLoadDataset() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/etl/task/ID")
            .respond()
                .withStatus(202)
                .withBody(readFromResource("/dataset/pullTask.json"))
            .thenRespond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusOk.json"));

        final DatasetManifest manifest = MAPPER.readValue(readFromResource("/dataset/datasetManifest.json"), DatasetManifest.class);
        gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
    }

    @Test
    public void shouldLoadDatasets() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/etl/task/ID")
            .respond()
                .withStatus(202)
                .withBody(readFromResource("/dataset/pullTask.json"))
            .thenRespond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusOk.json"));

        final DatasetManifest manifest = MAPPER.readValue(readFromResource("/dataset/datasetManifest.json"), DatasetManifest.class);
        final InputStream source = new ByteArrayInputStream(new byte[]{});
        manifest.setSource(source);

        gd.getDatasetService().loadDatasets(project, manifest).get();

    }

    @Test(expectedExceptions = DatasetException.class, expectedExceptionsMessageRegExp = ".*dataset.person.*Unable to load.*")
    public void shouldFailPolling() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/etl/task/ID")
                .respond()
                .withStatus(400);

        final DatasetManifest manifest = MAPPER.readValue(readFromResource("/dataset/datasetManifest.json"), DatasetManifest.class);
        gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
    }

    @Test
    public void shouldFailLoading() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/etl/task/ID")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusError.json"));
        final DatasetManifest manifest = MAPPER.readValue(readFromResource("/dataset/datasetManifest.json"), DatasetManifest.class);
        try {
            gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
            fail("Exception should be thrown");
        } catch (DatasetException e) {
            assertThat(e.getMessage(), is("Load datasets [dataset.person] failed: status: ERROR"));
        }
    }

    @Test
    public void shouldReadErrorMessages() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/etl/task/ID")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusError.json"));
        onRequest()
                .havingPath(containsString("upload_status.json"))
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/failStatusComplex.json"));
        onRequest()
                .havingPath(containsString("d_adstpch_querynum.csv.log"))
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new String[]{"Very error indeed"}));

        final DatasetManifest manifest = MAPPER.readValue(readFromResource("/dataset/datasetManifest.json"), DatasetManifest.class);
        try {
            gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
            fail("Exception should be thrown");
        } catch (DatasetException e) {
            assertThat(e.getMessage(), is("Load datasets [dataset.person] failed: [Very error indeed, 1 errors occured during csv import]"));
        }
    }

    @Test
    public void shouldGetDatasetManifest() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/ldm/singleloadinterface/foo/manifest")
            .respond()
                .withBody(readFromResource("/dataset/datasetManifest.json"))
        ;

        final DatasetManifest manifest = gd.getDatasetService().getDatasetManifest(project, "foo");
        assertThat(manifest, is(notNullValue()));
    }

    @Test
    public void shouldListDatasets() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/ldm/singleloadinterface")
            .respond()
                .withBody(readFromResource("/dataset/datasets.json"))
        ;

        final Collection<Dataset> datasets = gd.getDatasetService().listDatasets(project);
        assertThat(datasets, hasSize(1));
    }

    @Test
    public void shouldOptimizeSliHash() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/etl/mode")
                .respond()
                .withStatus(202)
                .withBody("{\"uri\" : \"" + STATUS_URI + "\"}");
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
                .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new TaskStatus("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new TaskStatus("OK", STATUS_URI)));

        gd.getDatasetService().optimizeSliHash(project).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailOptimizeSliHash() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/etl/mode")
                .respond()
                .withStatus(202)
                .withBody("{\"uri\" : \"" + STATUS_URI + "\"}");
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
                .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new TaskStatus("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new TaskStatus("ERROR", STATUS_URI)));

        gd.getDatasetService().optimizeSliHash(project).get();
    }

    @Test
    public void shouldUpdateProjectData() throws IOException {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/dml/manage")
                .respond()
                .withStatus(202)
                .withBody("{\"uri\" : \"" + STATUS_URI + "\"}");
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
                .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new TaskState("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new TaskState("OK", STATUS_URI)))
        ;

        gd.getDatasetService().updateProjectData(project, DML_MAQL).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailUpdateProjectData() throws IOException {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/dml/manage")
                .respond()
                .withStatus(202)
                .withBody("{\"uri\" : \"" + STATUS_URI + "\"}");
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
                .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new TaskState("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new TaskState("ERROR", STATUS_URI)))
        ;

        gd.getDatasetService().updateProjectData(project, DML_MAQL).get();
    }
}