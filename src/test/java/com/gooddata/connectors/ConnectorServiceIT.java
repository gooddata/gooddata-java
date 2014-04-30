package com.gooddata.connectors;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.GoodDataException;
import com.gooddata.gdc.UriResponse;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectorServiceIT extends AbstractGoodDataIT {
    private Project project;
    private ConnectorService connectors;

    @BeforeMethod
    public void setUp() throws Exception {
        project = MAPPER.readValue(readResource("/project/project.json"), Project.class);
        connectors = gd.getConnectorService();
    }

    @Test
    public void shouldCreateIntegration() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/templates")
            .respond()
                .withBody(readResource("/project/project-templates.json"));

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withBody(readResource("/connectors/integration.json"));

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/settings")
            .respond()
                .withStatus(200);

        final Zendesk4Settings settings = new Zendesk4Settings("http://zendesk");
        final Integration integration = connectors.createIntegration(project, settings);
        assertThat(integration, notNullValue());
    }

    @Test
    public void shouldExecuteProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes")
            .respond()
                .withBody(MAPPER.writeValueAsString(new UriResponse("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")));
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")
            .respond()
                .withBody(readResource("/connectors/process-scheduled.json"))
            .thenRespond()
                .withBody(readResource("/connectors/process-finished.json"))
        ;

        final Process process = connectors.executeProcess(project, new Zendesk4Process()).get();
        assertThat(process.getStatus().getCode(), is("SYNCHRONIZED"));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailExecuteProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes")
            .respond()
                .withBody(MAPPER.writeValueAsString(new UriResponse("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")));
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")
            .respond()
                .withBody(readResource("/connectors/process-error.json"));

        connectors.executeProcess(project, new Zendesk4Process()).get();
    }
}