/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.projecttemplate;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

@SuppressWarnings("deprecation")
public class TemplateTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Template template = readObjectFromResource("/projecttemplate/template.json", Template.class);
        assertThat(template, is(notNullValue()));
        assertThat(template.getUri(), is("/projectTemplates/ZendeskAnalytics/20"));
        assertThat(template.getUrn(), is("urn:gooddata:ZendeskAnalytics"));
        assertThat(template.getVersion(), is("20"));
        assertThat(template.getHidden(), is(Boolean.TRUE));
        assertThat(template.getMdDefinitionUri(), is("/projectTemplates/ZendeskAnalytics/20/ZendeskAnalytics.json"));
        assertThat(template.getDwDefinitionUri(), is("/projectTemplates/ZendeskAnalytics/20/ZendeskAnalytics.sql"));
        assertThat(template.getConfigUri(), is("/projectTemplates/ZendeskAnalytics/20/config.json"));
        assertThat(template.getManifestsUris(), hasSize(1));

        final Template.Meta meta = template.getMeta();
        assertThat(meta, is(notNullValue()));
        assertThat(meta.getSummary(), is("Zendesk Analytics Template"));
        assertThat(meta.getTitle(), is("Zendesk Analytics Template"));
        assertThat(meta.getApiVersion(), is("1.1"));
        assertThat(meta.getCategory(), is("projectTemplate"));

        final Template.Author author = meta.getAuthor();
        assertThat(author, is(notNullValue()));
        assertThat(author.getName(), is("GoodData"));
        assertThat(author.getUri(), is("http://www.gooddata.com/"));
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(Template.class)
                .usingGetClass()
                .withOnlyTheseFields("uri")
                .verify();
    }

}