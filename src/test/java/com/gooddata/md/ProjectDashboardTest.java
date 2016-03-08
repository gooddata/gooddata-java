package com.gooddata.md;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.testng.annotations.Test;

public class ProjectDashboardTest {

    @Test
    public void testDeserialization() throws Exception {
        final ProjectDashboard dashboard = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/projectDashboard.json"), ProjectDashboard.class);

        assertThat(dashboard.getUri(), is("/gdc/md/PROJECT_ID/obj/12345"));

        assertThat(dashboard.getTabs(), notNullValue());
        assertThat(dashboard.getTabs().size(), is(2));

        final ProjectDashboard.Tab tab1 = dashboard.getTabByName("Tab 1");
        assertThat(tab1, notNullValue());
        assertThat(tab1.getIdentifier(), is("tab1abc"));

        assertThat(dashboard.getTabByName("Undefined"), nullValue());
    }
}