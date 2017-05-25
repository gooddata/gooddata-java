/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.projecttemplate;

import com.gooddata.dataset.DatasetManifest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

public class ProjectTemplateServiceTest {

    private static final String TEMPLATE_URI = "/projectTemplates/ZendeskAnalytics/20";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Template template;
    @Mock
    private Templates templates;
    @Mock
    private DatasetManifest manifest;

    private ProjectTemplateService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ProjectTemplateService(restTemplate);
        when(templates.getTemplates()).thenReturn(singletonList(template));
    }

    @Test
    public void testGetTemplate() throws Exception {
        when(restTemplate.getForObject(TEMPLATE_URI, Template.class))
                .thenReturn(template);
        final Template template = service.getTemplateByUri(TEMPLATE_URI);
        assertThat(template, is(template));
    }

    @Test
    public void testGetTemplates() throws Exception {
        when(restTemplate.getForObject("/projectTemplates", Templates.class))
                .thenReturn(templates);

        final Collection<Template> templates = service.getTemplates();
        assertThat(templates, hasSize(1));
        assertThat(templates.iterator().next(), is(template));
    }

    @Test
    public void testGetManifests() throws Exception {
        when(restTemplate.getForObject(TEMPLATE_URI, Template.class))
                .thenReturn(template);
        when(template.getManifestsUris()).thenReturn(singletonList("/manifestUri"));
        when(restTemplate.getForObject("/manifestUri", DatasetManifest.class)).thenReturn(manifest);

        final Collection<DatasetManifest> manifests = service.getManifests(template);
        assertThat(manifests, hasSize(1));
        assertThat(manifests.iterator().next(), is(manifest));
    }
}