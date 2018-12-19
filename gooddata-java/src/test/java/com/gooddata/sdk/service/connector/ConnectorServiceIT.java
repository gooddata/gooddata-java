/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.connector;

import com.gooddata.GoodDataException;
import com.gooddata.sdk.model.connector.*;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.sdk.model.connector.Status.Code.ERROR;
import static com.gooddata.sdk.model.connector.Status.Code.SYNCHRONIZED;
import static com.gooddata.util.ResourceUtils.*;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;

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
    public void shouldCreateIntegration() throws Exception {
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
    public void shouldGetIntegration() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withBody(readFromResource("/connector/integration.json"));

        final Integration integration = connectors.getIntegration(project, ConnectorType.ZENDESK4);
        assertThat(integration, notNullValue());
    }

    @Test(expectedExceptions = IntegrationNotFoundException.class)
    public void shouldFailGetIntegrationNotFount() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration")
            .respond()
                .withStatus(404);

        connectors.getIntegration(project, ConnectorType.ZENDESK4);
    }

    @Test
    public void shouldUpdateIntegration() throws Exception {
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
    public void shouldGetProcessStatus() throws Exception {
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
    public void shouldFailGetProcessStatusPolling() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID")
            .respond()
                .withStatus(400);

        connectors.getProcessStatus(runningProcess).get();
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailGetProcessStatus() throws Exception {
        onRequest()
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/processes/PROCESS_ID")
            .respond()
                .withBody(readFromResource("/connector/process-status-error.json"));

        final ProcessStatus process = connectors.getProcessStatus(runningProcess).get();
        assertThat(process.getStatus().getCode(), is(ERROR.name()));
    }

    @Test
    public void shouldGetZendesk4Settings() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/zendesk4/integration/settings")
            .respond()
                .withBody(readFromResource("/connector/settings-zendesk4.json"));

        final Zendesk4Settings zendesk4Settings = connectors.getZendesk4Settings(project);
        assertThat(zendesk4Settings, jsonEquals(resource("connector/settings-zendesk4.json")));
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldGetSettingsNotFound() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(404);

        connectors.getSettings(project, ConnectorType.ZENDESK4, Zendesk4Settings.class);
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldUpdateSettingsNotFound() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
             .respond()
                .withStatus(404);

        connectors.updateSettings(project, new CoupaSettings("UTC"));
    }

    @Test
    public void shouldGetCoupaSettings() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings")
             .respond()
                .withBody(readFromResource("/connector/settings-coupa.json"));

        final CoupaSettings coupaSettings = connectors.getCoupaSettings(project);
        assertThat(coupaSettings, jsonEquals(resource("connector/settings-coupa.json")));
    }

    @Test
    public void shouldCreateCoupaInstance() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances")
             .respond()
                .withStatus(201)
                .withBody("{\"uri\":\"/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances/123\"}");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances/123")
             .respond()
                .withBody(readFromResource("/connector/coupa_instance.json"));

        final CoupaInstance instance = connectors.createCoupaInstance(project, new CoupaInstance("i1", "url", "key"));
        assertThat(instance, notNullValue());
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldFailCreatingCoupaInstance() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances")
             .respond()
                .withStatus(404);

        connectors.createCoupaInstance(project, new CoupaInstance("i1", "url", "key"));
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldFailGettingCreatedCoupaInstance() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances")
             .respond()
                .withStatus(201)
                .withBody("{\"uri\":\"/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances/123\"}");

        //method should throw exception for any error response status returned by GET request on created Coupa instance uri
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances/123")
             .respond()
                .withStatus(404);

        connectors.createCoupaInstance(project, new CoupaInstance("i1", "url", "key"));
    }

    @Test
    public void shouldGetCoupaInstances() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances")
             .respond()
                .withBody(readFromResource("/connector/coupa_instances.json"));

        final Collection<CoupaInstance> instances = connectors.findCoupaInstances(project);

        assertThat(instances, hasItem(new CoupaInstance("instance 1", "https://gooddata-demo01.coupacloud.com/api", null)));
    }

    @Test
    public void shouldGetEmptyCollectionWhenNoCoupaInstances() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances")
             .respond()
                .withBody(readFromResource("/connector/coupa_instances-empty.json"));

        final Collection<CoupaInstance> instances = connectors.findCoupaInstances(project);

        assertThat(instances, is(emptyCollectionOf(CoupaInstance.class)));
    }

    @Test(expectedExceptions = ConnectorException.class)
    public void shouldFailGettingCoupaInstances() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/coupa/integration/config/settings/instances")
             .respond()
                .withStatus(404);

        connectors.findCoupaInstances(project);
    }

    @Test
    public void shouldGetPardotSettings() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/projects/PROJECT_ID/connectors/pardot/integration/config/settings")
             .respond()
                .withBody(readFromResource("/connector/settings-pardot.json"));

        final PardotSettings pardotSettings = connectors.getPardotSettings(project);
        assertThat(pardotSettings, jsonEquals(resource("connector/settings-pardot.json")));
    }
}
