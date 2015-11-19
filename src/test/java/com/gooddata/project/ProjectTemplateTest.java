/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ProjectTemplateTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final ProjectTemplate template = new ObjectMapper()
                .readValue(getClass().getResource("/project/project-template.json"), ProjectTemplate.class);

        assertThat(template, is(notNullValue()));
        assertThat(template.getUrl(), is("/projectTemplates/ZendeskAnalytics/11"));
        assertThat(template.getUrn(), is("urn:gooddata:ZendeskAnalytics"));
        assertThat(template.getVersion(), is("11"));
    }
}