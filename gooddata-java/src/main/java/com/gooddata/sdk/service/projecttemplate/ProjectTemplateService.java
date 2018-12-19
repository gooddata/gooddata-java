/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.projecttemplate;

import com.gooddata.sdk.service.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.dataset.DatasetManifest;
import com.gooddata.sdk.model.projecttemplate.Template;
import com.gooddata.sdk.model.projecttemplate.Templates;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Service enabling access to project templates, under /projectTemplates/...
 */
public class ProjectTemplateService extends AbstractService {

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public ProjectTemplateService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     *
     * @param restTemplate RESTful HTTP Spring template
     * @deprecated use ProjectTemplateService(RestTemplate) constructor instead
     */
    @Deprecated
    public ProjectTemplateService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * List of all projects' templates
     * @return list of templates
     */
    public Collection<Template> getTemplates() {
        try {
            return restTemplate.getForObject(Templates.URI, Templates.class).getTemplates();
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get templates", e);
        }
    }

    /**
     * Get project template by given uri.
     * @param uri uri of the template
     * @return project template
     */
    public Template getTemplateByUri(String uri) {
        notEmpty(uri, "template uri");
        try {
            return restTemplate.getForObject(uri, Template.class);
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get template of uri=" + uri, e);
        }
    }

    /**
     * Get manifests of given template
     * @param template template
     * @return manifests linked from project template
     */
    public Collection<DatasetManifest> getManifests(Template template) {
        notNull(template, "template");
        notNull(template.getManifestsUris(), "template.manifestsUris");
        try {
            return template.getManifestsUris().stream()
                    .map(manifestUri -> restTemplate.getForObject(manifestUri, DatasetManifest.class))
                    .collect(Collectors.toList());
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to get manifests for template of uri=" + template.getUri(), e);
        }
    }
}
