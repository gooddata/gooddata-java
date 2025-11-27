/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.projecttemplate;

import com.gooddata.sdk.model.dataset.DatasetManifest;
import com.gooddata.sdk.model.projecttemplate.Template;
import com.gooddata.sdk.model.projecttemplate.Templates;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("deprecation")
public class ProjectTemplateServiceIT extends AbstractGoodDataIT {

    private static final String TEMPLATE_URI = "/projectTemplates/ZendeskAnalytics/20";

    private Template template;

    private static void onTemplateRequest() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(TEMPLATE_URI)
                .respond()
                .withBody(readFromResource("/projecttemplate/template.json"))
                .withStatus(200);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        template = readObjectFromResource("/projecttemplate/template.json", Template.class);
    }

    @Test
    public void shouldGetTemplate() throws Exception {
        onTemplateRequest();

        final Template template = gd.getProjectTemplateService().getTemplateByUri(TEMPLATE_URI);
        assertThat(template, is(template));
    }

    @Test
    public void shouldGetTemplates() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(Templates.URI)
                .respond()
                .withBody(readFromResource("/projecttemplate/templates.json"))
                .withStatus(200);

        final Collection<Template> templates = gd.getProjectTemplateService().getTemplates();
        assertThat(templates, hasSize(1));
        assertThat(templates.iterator().next(), is(template));
    }

    @Test
    public void shouldGetManifests() throws Exception {
        onTemplateRequest();
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/projectTemplates/ZendeskAnalytics/20/dataset.zendesktickets.json")
                .respond()
                .withBody(readFromResource("/dataset/datasetManifest.json"))
                .withStatus(200);


        final Collection<DatasetManifest> manifests = gd.getProjectTemplateService().getManifests(template);
        assertThat(manifests, hasSize(1));
        assertThat(manifests.iterator().next().getDataSet(), is("dataset.person"));
    }
}