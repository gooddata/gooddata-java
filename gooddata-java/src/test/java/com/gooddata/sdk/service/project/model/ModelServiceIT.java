/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.project.model;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.model.gdc.AsyncTask;
import com.gooddata.sdk.model.gdc.TaskStatus;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.project.model.ModelDiff;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.lang.String.format;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelServiceIT extends AbstractGoodDataIT {

    private static final String DIFF_URI = "/gdc/projects/PROJECT_ID/model/diff";
    private static final String DIFF_POLL_URI = DIFF_URI + "/123";
    private static final String LDM_MANAGE2 = "/gdc/md/PROJECT_ID/ldm/manage2";
    private static final String STATUS_URI = "/gdc/md/PROJECT_ID/tasks/123/status";
    private static final String MODEL_DIFF_JSON = format("{\"projectModelDiff\":{\"updateScripts\":[" +
                    "{\"updateScript\":{\"preserveData\":true,\"cascadeDrops\":false,\"maqlDdlChunks\":[\"%s\",\"%s\"]}}]}}",
            "synchronize {dataset.chunk1} preserve data",
            "synchronize {dataset.chunk2} preserve data");

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
    }

    @Test
    public void shouldCreateDiff() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(DIFF_URI)
            .respond()
                .withStatus(202)
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask(DIFF_POLL_URI)));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(DIFF_POLL_URI)
            .respond()
                .withStatus(202)
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask(DIFF_POLL_URI)))
            .thenRespond()
                .withStatus(200)
                .withBody(readFromResource("/model/modelDiff.json"))
        ;

        final ModelDiff diff = gd.getModelService().getProjectModelDiff(project, "xxx").get();

        assertThat(diff, is(notNullValue()));
        assertThat(diff.getUpdateMaql().get(0), is("CREATE FOLDER {ffld.employee} VISUAL(TITLE \"Employee\") TYPE FACT;\nCREATE FACT {fact.employee.age} VISUAL(TITLE \"Employee Age\", FOLDER {ffld.employee}) AS {f_employee.f_age};\nALTER DATASET {dataset.employee} ADD {fact.employee.age};\nSYNCHRONIZE {dataset.employee} PRESERVE DATA;"));
    }

    @Test(expectedExceptions = ModelException.class, expectedExceptionsMessageRegExp = "Unable to get project model diff")
    public void shouldFailCreateDiff() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(DIFF_URI)
            .respond()
                .withStatus(202)
                .withBody(OBJECT_MAPPER.writeValueAsString(new AsyncTask(DIFF_POLL_URI)));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(DIFF_POLL_URI)
            .respond()
                .withStatus(400)
        ;

        gd.getModelService().getProjectModelDiff(project, "xxx").get();
    }

    @Test
    public void shouldUpdateModel() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(LDM_MANAGE2)
            .respond()
                .withStatus(202)
                .withBody(readFromResource("/model/maqlDdlLinks.json"));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
            .respond()
                .withStatus(202)
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("RUNNING", STATUS_URI)))
            .thenRespond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(new TaskStatus("OK", STATUS_URI)))
        ;

        final ModelDiff diff = OBJECT_MAPPER.readValue(MODEL_DIFF_JSON, ModelDiff.class);
        gd.getModelService().updateProjectModel(project, diff).get();
    }

    @Test(expectedExceptions = ModelException.class, expectedExceptionsMessageRegExp = "Unable to update project model")
    public void shouldFailUpdateModel() throws IOException {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(LDM_MANAGE2)
            .respond()
                .withStatus(202)
                .withBody(readFromResource("/model/maqlDdlLinks.json"));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
            .respond()
                .withStatus(400)
        ;

        final ModelDiff diff = OBJECT_MAPPER.readValue(MODEL_DIFF_JSON, ModelDiff.class);
        gd.getModelService().updateProjectModel(project, diff).get();
    }
}
