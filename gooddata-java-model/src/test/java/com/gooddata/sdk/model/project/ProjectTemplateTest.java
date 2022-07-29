/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ProjectTemplateTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final ProjectTemplate template = readObjectFromResource("/project/project-template.json", ProjectTemplate.class);

        assertThat(template, is(notNullValue()));
        assertThat(template.getUrl(), is("/projectTemplates/ZendeskAnalytics/11"));
        assertThat(template.getUrn(), is("urn:gooddata:ZendeskAnalytics"));
        assertThat(template.getVersion(), is("11"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final ProjectTemplate template = readObjectFromResource("/project/project-template.json", ProjectTemplate.class);

        assertThat(template.toString(), matchesPattern(ProjectTemplate.class.getSimpleName() + "\\[.*\\]"));
    }
}