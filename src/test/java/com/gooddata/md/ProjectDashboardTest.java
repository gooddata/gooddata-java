/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.gooddata.md.ProjectDashboard.Tab;
import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProjectDashboardTest {

    @Test
    public void testDeserialization() throws Exception {
        final ProjectDashboard dashboard = readObjectFromResource("/md/projectDashboard.json", ProjectDashboard.class);

        assertThat(dashboard.getUri(), is("/gdc/md/PROJECT_ID/obj/12345"));

        assertThat(dashboard.getTabs(), notNullValue());
        assertThat(dashboard.getTabs().size(), is(2));

        final Tab tab1 = dashboard.getTabByName("Tab 1");
        assertThat(tab1, notNullValue());
        assertThat(tab1.getIdentifier(), is("tab1abc"));

        assertThat(dashboard.getTabByName("Undefined"), nullValue());
    }

    @Test
    public void shouldSerialize() throws Exception {
        final ProjectDashboard dashboard = new ProjectDashboard("my dashboard", new Tab("my tab"));
        assertThat(dashboard, jsonEquals(resource("md/projectDashboardCreate.json")));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final ProjectDashboard dashboard = readObjectFromResource("/md/projectDashboard.json", ProjectDashboard.class);

        assertThat(dashboard.toString(), matchesPattern(ProjectDashboard.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final ProjectDashboard dashboard = readObjectFromResource("/md/projectDashboard.json", ProjectDashboard.class);
        final ProjectDashboard deserialized = SerializationUtils.roundtrip(dashboard);

        assertThat(deserialized, jsonEquals(dashboard));
    }
}