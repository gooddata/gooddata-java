package com.gooddata.model;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.gdc.TaskStatus;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.gooddata.model.ModelDiff.UpdateScript;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ModelServiceIT extends AbstractGoodDataIT {

    private static final String DIFF_URI = "/gdc/projects/PROJECT_ID/model/diff";
    private static final String DIFF_POLL_URI = DIFF_URI + "/123";
    private static final String LDM_MANAGE2 = "/gdc/md/PROJECT_ID/ldm/manage2";
    private static final String STATUS_URI = "/gdc/md/PROJECT_ID/tasks/123/status";

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readResource("/project/project.json"), Project.class);
    }

    @Test
    public void shouldCreateDiff() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(DIFF_URI)
            .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new AsyncTask(DIFF_POLL_URI)));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(DIFF_POLL_URI)
            .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new AsyncTask(DIFF_POLL_URI)))
            .thenRespond()
                .withStatus(200)
                .withBody(readResource("/model/modelDiff.json"))
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
                .withBody(MAPPER.writeValueAsString(new AsyncTask(DIFF_POLL_URI)));
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
                .withBody(readResource("/model/maqlDdlLinks.json"));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
            .respond()
                .withStatus(202)
                .withBody(MAPPER.writeValueAsString(new TaskStatus("RUNNING", STATUS_URI)))
            .thenRespond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new TaskStatus("OK", STATUS_URI)))
        ;

        final ModelDiff diff = new ModelDiff(new UpdateScript(true, false,
                "synchronize {dataset.chunk1} preserve data",
                "synchronize {dataset.chunk2} preserve data"
        ));
        gd.getModelService().updateProjectModel(project, diff).get();
    }

    @Test(expectedExceptions = ModelException.class, expectedExceptionsMessageRegExp = "Unable to update project model")
    public void shouldFailUpdateModel() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(LDM_MANAGE2)
            .respond()
                .withStatus(202)
                .withBody(readResource("/model/maqlDdlLinks.json"));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(STATUS_URI)
            .respond()
                .withStatus(400)
        ;

        final ModelDiff diff = new ModelDiff(new UpdateScript(true, false,
                "synchronize {dataset.chunk1} preserve data",
                "synchronize {dataset.chunk2} preserve data"
        ));
        gd.getModelService().updateProjectModel(project, diff).get();
    }
}