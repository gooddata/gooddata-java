/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class IntegrationTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Integration integration = readObjectFromResource("/connector/integration.json", Integration.class);

        assertThat(integration, is(notNullValue()));
        assertThat(integration.isActive(), is(true));
        assertThat(integration.getProjectTemplate(), is("/projectTemplates/template"));
        assertThat(integration.getLastFinishedProcess(), is(notNullValue()));
        assertThat(integration.getLastSuccessfulProcess(), is(notNullValue()));
        assertThat(integration.getRunningProcess(), is(nullValue()));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final Integration integration = new Integration("template");
        assertThat(integration, jsonEquals(resource("connector/integration-in.json")));
    }

    @Test
    public void testSimplifiedConstructor() throws Exception {
        final Integration integration = new Integration("template");

        assertThat(integration.getProjectTemplate(), is("template"));
        assertThat(integration.isActive(), is(true));
    }

    @Test
    public void testToStringFormat() {
        final Integration integration = new Integration("template");

        assertThat(integration.toString(), matchesPattern(Integration.class.getSimpleName() + "\\[.*\\]"));
    }

}