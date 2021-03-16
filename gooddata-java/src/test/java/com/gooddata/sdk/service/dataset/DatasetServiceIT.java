/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataset;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.dataset.*;
import com.gooddata.sdk.model.gdc.AboutLinks.Link;
import com.gooddata.sdk.model.gdc.TaskStatus;
import com.gooddata.sdk.model.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.fail;

public class DatasetServiceIT extends AbstractGoodDataIT {

    private static final String STATUS_URI = "/gdc/md/PROJECT_ID/tasks/TASK_ID/status";

    private static final String DML_MAQL = "DELETE FROM {attr.logs.phase_id} WHERE {created.date.yyyymmdd} < \"2015-01-18\"";

    private Project project;

    @BeforeClass
    public void setUpClass() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
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
                .havingPath(startsWith("/uploads/"))
                .havingMethodEqualTo("DELETE")
            .respond()
                .withStatus(200);
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/etl/pull2")
                .havingMethodEqualTo("POST")
            .respond()
                .withStatus(201)
                .withBody(readFromResource("/dataset/pullTask.json"));
    }

    @Test
    public void shouldLoadDataset() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/tasks/task/ID/status")
            .respond()
                .withStatus(202)
                .withBody(readFromResource("/dataset/pullTaskStatusRunning.json"))
            .thenRespond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusOk.json"));

        final DatasetManifest manifest = readObjectFromResource("/dataset/datasetManifest.json", DatasetManifest.class);
        gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
    }

    @Test
    public void shouldLoadDatasets() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/tasks/task/ID/status")
            .respond()
                .withStatus(202)
                .withBody(readFromResource("/dataset/pullTaskStatusRunning.json"))
            .thenRespond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusOk.json"));

        final DatasetManifest manifest = readObjectFromResource("/dataset/datasetManifest.json", DatasetManifest.class);
        final InputStream source = new ByteArrayInputStream(new byte[]{});
        manifest.setSource(source);

        gd.getDatasetService().loadDatasets(project, manifest).get();

    }

    @Test(expectedExceptions = DatasetException.class, expectedExceptionsMessageRegExp = ".*dataset.person.*Unable to load.*")
    public void shouldFailPolling() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/tasks/task/ID/status")
                .respond()
                .withStatus(400);

        final DatasetManifest manifest = readObjectFromResource("/dataset/datasetManifest.json", DatasetManifest.class);
        gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
    }

    @Test
    public void shouldFailLoading() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT/tasks/task/ID/status")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/pullTaskStatusError.json"));
        final DatasetManifest manifest = readObjectFromResource("/dataset/datasetManifest.json", DatasetManifest.class);
        try {
            gd.getDatasetService().loadDataset(project, manifest, new ByteArrayInputStream(new byte[]{})).get();
            fail("Exception should be thrown");
        } catch (DatasetException e) {
            assertThat(e.getMessage(), is("Load datasets [dataset.person] failed: [status: ERROR. Missing field [attr.person.age]]"));
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
    public void shouldListDatasetLinks() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/ldm/singleloadinterface")
            .respond()
                .withBody(readFromResource("/dataset/datasetLinks.json"))
        ;

        final Collection<Link> datasets = gd.getDatasetService().listDatasetLinks(project);
        assertThat(datasets, hasSize(1));
        assertThat(datasets.iterator().next().getUri(),
                is("/gdc/md/PROJECT_ID/ldm/singleloadinterface/dataset.person"));
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
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("OK", STATUS_URI)));

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
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("ERROR", STATUS_URI)));

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
                .withStatus(200) // REST API returns HTTP 200 when task is in RUNNING state :(
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskState("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskState("OK", STATUS_URI)))
        ;

        gd.getDatasetService().updateProjectData(project, DML_MAQL).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailUpdateProjectDataServerError() throws IOException {
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
                .withStatus(500)
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
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskState("RUNNING", STATUS_URI)))
                .thenRespond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskState("ERROR", STATUS_URI)))
        ;

        gd.getDatasetService().updateProjectData(project, DML_MAQL).get();
    }

    @Test
    public void shouldListUploadsForDataset() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/data/sets")
        .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/uploads/data-sets.json"));

        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/data/uploads/814")
        .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/uploads/uploads.json"));

        final Collection<Upload> uploads = gd.getDatasetService().listUploadsForDataset(project, "dataset.campaign");

        assertThat(uploads, notNullValue());
        assertThat(uploads, hasSize(2));
    }

    @Test
    public void shouldGetLastUploadForDataset() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/data/sets")
        .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/uploads/data-sets.json"));

        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/data/upload/1076")
        .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/uploads/upload.json"));

        final Upload upload = gd.getDatasetService().getLastUploadForDataset(project, "dataset.campaign");

        assertThat(upload, notNullValue());
        assertThat(upload.getStatus(), is("OK"));
    }

    @Test
    public void shouldGetUploadStatistics() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/data/uploads_info")
        .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/uploads/data-uploads-info.json"));

        final UploadStatistics uploadStatistics = gd.getDatasetService().getUploadStatistics(project);

        assertThat(uploadStatistics, notNullValue());
        assertThat(uploadStatistics.getUploadsCount("OK"), is(845));
    }
}