package com.gooddata.model;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.gooddata.model.ModelDiff.UpdateScript;
import static net.jadler.Jadler.onRequest;

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
                .withBody(MAPPER.writeValueAsString(new MaqlDdlTaskStatus("RUNNING", STATUS_URI)))
            .thenRespond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new MaqlDdlTaskStatus("OK", STATUS_URI)))
        ;

        final ModelDiff diff = new ModelDiff(new UpdateScript(true, false,
                "synchronize {dataset.person} preserve data",
                "synchronize {dataset.person} preserve data"
        ));
        gd.getModelService().updateProjectModel(project, diff).get();
    }
}