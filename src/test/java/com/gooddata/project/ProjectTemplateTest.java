/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

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

    @Test
    public void testToStringFormat() throws Exception {
        final ProjectTemplate template = new ObjectMapper()
                .readValue(getClass().getResource("/project/project-template.json"), ProjectTemplate.class);

        assertThat(template.toString(), matchesPattern(ProjectTemplate.class.getSimpleName() + "\\[.*\\]"));
    }
}