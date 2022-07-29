/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.connector;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.connector.*;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static com.gooddata.sdk.model.connector.Status.Code.ERROR;
import static com.gooddata.sdk.model.connector.Status.Code.SYNCHRONIZED;
import static com.gooddata.sdk.common.util.ResourceUtils.*;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConnectorServiceIT extends AbstractGoodDataIT {
    private Project project;
    private ConnectorService connectors;
    private IntegrationProcessStatus runningProcess;

    @BeforeMethod
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
        connectors = gd.getConnectorService();
        runningProcess = OBJECT_MAPPER.readValue(
                "{\"status\":null,\"started\":null,\"finished\":null,\"links\":{\"self\":\"/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID\"}}", IntegrationProcessStatus.class);
    }

    @Test
    public void shouldCreateIntegration() {
        onRequest()
                .havingPathEqualTo("/gdc/md/PROJECT_ID/templates")
            .respond()
                .withBody(readFromResource("/project/project-templates.json"));

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withBody(readFromResource("/connector/integration.json"));

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
    public void shouldGetIntegration() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withBody(readFromResource("/connector/integration.json"));

        final Integration integration = connectors.getIntegration(project, ConnectorType.ZENDESK4);
        assertThat(integration, notNullValue());
    }

    @Test(expectedExceptions = IntegrationNotFoundException.class)
    public void shouldFailGetIntegrationNotFound() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withStatus(404);

        connectors.getIntegration(project, ConnectorType.ZENDESK4);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void shouldFailGetIntegrationInternalServerError() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withStatus(500);

        connectors.getIntegration(project, ConnectorType.ZENDESK4);
    }

    @Test
    public void shouldUpdateIntegration() {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withBody(readFromResource("/connector/integration.json"));

        final Integration integration = new Integration("/projectTemplates/template");
        connectors.updateIntegration(project, ConnectorType.ZENDESK4, integration);
    }

    @Test(expectedExceptions = IntegrationNotFoundException.class)
    public void shouldFailUpdateIntegrationNotFound() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withStatus(404);

        final Integration integration = new Integration("/projectTemplates/template");
        connectors.updateIntegration(project, ConnectorType.ZENDESK4, integration);
    }
    
    @Test
    public void shouldDeleteIntegration() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
           .respond()
                .withStatus(204);
        
        connectors.deleteIntegration(project, ConnectorType.ZENDESK4);
    }
    
    @Test(expectedExceptions = IntegrationNotFoundException.class)
    public void shouldFailDeleteIntegrationNotFound() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
          .respond()
                .withStatus(404);

        connectors.deleteIntegration(project, ConnectorType.ZENDESK4);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void shouldFailDeleteIntegrationInternalServerError() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withStatus(500);

        connectors.deleteIntegration(project, ConnectorType.ZENDESK4);
    }

    @Test
    public void shouldExecuteProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes")
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")));
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")
            .respond()
                .withBody(readFromResource("/connector/process-status-scheduled.json"))
            .thenRespond()
                .withBody(readFromResource("/connector/process-status-finished.json"))
        ;

        final ProcessStatus process = connectors.executeProcess(project, new Zendesk4ProcessExecution()).get();
        assertThat(process.getStatus().getCode(), is(SYNCHRONIZED.name()));
    }

    @Test(expectedExceptions = ConnectorException.class, expectedExceptionsMessageRegExp = ".*zendesk4 process PROCESS failed.*")
    public void shouldFailExecuteProcessPolling() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes")
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")));
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")
            .respond()
                .withStatus(400)
        ;
        connectors.executeProcess(project, new Zendesk4ProcessExecution()).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailExecuteProcess() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes")
            .respond()
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")));
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS")
            .respond()
                .withBody(readFromResource("/connector/process-status-error.json"));

        final ProcessStatus process = connectors.executeProcess(project, new Zendesk4ProcessExecution()).get();
        assertThat(process.getStatus().getCode(), is(ERROR.name()));
    }

    @Test
    public void shouldGetProcessStatus() {
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID")
            .respond()
                .withBody(readFromResource("/connector/process-status-scheduled.json"))
            .thenRespond()
                .withBody(readFromResource("/connector/process-status-finished.json"));

        final ProcessStatus process = connectors.getProcessStatus(runningProcess).get();
        assertThat(process.getStatus().getCode(), is(SYNCHRONIZED.name()));
    }

    @Test(expectedExceptions = ConnectorException.class, expectedExceptionsMessageRegExp = ".*zendesk4 process PROCESS_ID failed.*")
    public void shouldFailGetProcessStatusPolling() {
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID")
            .respond()
                .withStatus(400);

        connectors.getProcessStatus(runningProcess).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailGetProcessStatus() {
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID")
            .respond()
                .withBody(readFromResource("/connector/process-status-error.json"));

        final ProcessStatus process = connectors.getProcessStatus(runningProcess).get();
        assertThat(process.getStatus().getCode(), is(ERROR.name()));
    }

    @Test
    public void shouldGetZendesk4Settings() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/settings")
            .respond()
                .withBody(readFromResource("/connector/settings-zendesk4.json"));

        final Zendesk4Settings zendesk4Settings = connectors.getZendesk4Settings(project);
        assertThat(zendesk4Settings, jsonEquals(resource("connector/settings-zendesk4.json")));
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldGetSettingsNotFound() {
        onRequest()
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(404);

        connectors.getSettings(project, ConnectorType.ZENDESK4, Zendesk4Settings.class);
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldUpdateSettingsNotFound() {
        onRequest()
                .havingMethodEqualTo("GET")
             .respond()
                .withStatus(404);

        connectors.updateSettings(project, new Zendesk4Settings("http://zendesk"));
    }

    @Test
    public void shouldScheduleZendesk4Reload() {
        final Reload reload = mockAndScheduleReload(201);
        assertReload(reload);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void shouldScheduleZendesk4ReloadServerError() {
        mockAndScheduleReload(500);
    }

    private Reload mockAndScheduleReload(final int httpStatus) {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/reloads")
                .havingBody(jsonEquals(readStringFromResource("/connector/reload-in.json")))
                .respond()
                .withBody(readFromResource("/connector/reload.json"))
                .withStatus(httpStatus);

        final Map<String, Long> startTimes = new HashMap<>();
        startTimes.put(Reload.AGENT_TIMELINE_START_TIME_PROPERTY, 0L);
        startTimes.put(Reload.CHATS_START_TIME_PROPERTY, 123L);

        return connectors.scheduleZendesk4Reload(project, new Reload(startTimes));
    }

    @Test
    public void shouldGetZendesk4Reload() {
        final Reload reload = mockAndGetReload(200);

        assertReload(reload);
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldGetZendesk4ReloadNotFound() {
        mockAndGetReload(404);
    }

    private Reload mockAndGetReload(final int httpStatus) {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/reloads/21")
            .respond()
                .withBody(readFromResource("/connector/reload.json"))
                .withStatus(httpStatus);

        final Map<String, String> links = new HashMap<>();
        links.put("self", "/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/reloads/21");

        return connectors.getZendesk4Reload(new Reload(21, null, Reload.STATUS_DO, null, links));
    }

    private void assertReload(final Reload reload) {
        assertThat(reload, notNullValue());
        assertThat(reload.getId(), is(21));
        assertThat(reload.getStartTimes(), notNullValue());
        assertThat(reload.getStartTimes().size(), is(2));
        assertThat(reload.getAgentTimelineStartTime(), is(0L));
        assertThat(reload.getChatsStartTime(), is(123L));
        assertThat(reload.getStatus(), is(Reload.STATUS_RUNNING));
        assertThat(reload.getProcessId(), is("PROCESS_ID"));
        assertThat(reload.getLinks(), notNullValue());
        assertThat(reload.getLinks().size(), is(3));
        assertThat(reload.getLinks().get("self"), is("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/reloads/21"));
        assertThat(reload.getLinks().get("integration"), is("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration"));
        assertThat(reload.getLinks().get("process"), is("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID"));
    }


}
