package com.gooddata.connectors;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class IntegrationTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Integration integration = new ObjectMapper()
                .readValue(getClass().getResourceAsStream("/connectors/integration.json"), Integration.class);
        assertThat(integration, is(notNullValue()));
        assertThat(integration.isActive(), is(true));
        assertThat(integration.getProjectTemplate(), is("/projectTemplates/template"));
        assertThat(integration.getLastFinishedProcess(), is(notNullValue()));
        assertThat(integration.getLastSuccessfulProcess(), is(notNullValue()));
        assertThat(integration.getRunningProcess(), is(nullValue()));
    }
}