/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.projecttemplate;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.dataset.DatasetManifest;
import com.gooddata.sdk.model.projecttemplate.Template;
import com.gooddata.sdk.model.projecttemplate.Templates;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Service enabling access to project templates, under /projectTemplates/...
 *
 * @deprecated The project templates are deprecated and stopped working on May 15, 2019.
 * See https://support.gooddata.com/hc/en-us/articles/360016126334-April-4-2019
 * Deprecated since version 3.0.1. Will be removed in one of future versions.
 */
@Deprecated
public class ProjectTemplateService extends AbstractService {

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public ProjectTemplateService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * List of all projects' templates
     * @return list of templates
     */
    public Collection<Template> getTemplates() {
        try {
            final Templates templates = restTemplate.getForObject(Templates.URI, Templates.class);
            return templates != null ? templates.getTemplates() : Collections.emptyList();
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
