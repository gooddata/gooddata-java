/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.model.dataload.OutputStage;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.dataload.processes.ProcessNotFoundException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import static com.gooddata.util.Validate.*;

/**
 * Service to manage output stage.
 */
public class OutputStageService extends AbstractService {

    protected static final UriTemplate OUTPUT_STAGE_TEMPLATE = new UriTemplate(OutputStage.URI);

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public OutputStageService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Get output stage by given URI.
     * @param uri output stage uri
     * @return output stage object
     * @throws ProcessNotFoundException when the process doesn't exist
     */
    public OutputStage getOutputStageByUri(final String uri) {
        notEmpty(uri, "uri");
        isTrue(OUTPUT_STAGE_TEMPLATE.matches(uri), "uri does not match output stage pattern: " + OUTPUT_STAGE_TEMPLATE.toString());
        try {
            return restTemplate.getForObject(uri, OutputStage.class);
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to get output stage " + uri, e);
        }
    }

    /**
     * Get output stage by given project.
     * @param project project to which the process belongs
     * @return output stage
     * @throws ProcessNotFoundException when the process doesn't exist
     */
    public OutputStage getOutputStage(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        return getOutputStageByUri(OUTPUT_STAGE_TEMPLATE.expand(project.getId()).toString());
    }

    /**
     * Update output stage.
     *
     * @param outputStage output stage
     * @return updated output stage
     */
    public OutputStage updateOutputStage(final OutputStage outputStage) {
        notNull(outputStage, "outputStage");
        notNull(outputStage.getUri(), "outputStage.uri");

        try {
            HttpEntity<OutputStage> outputStageHttpEntity = new HttpEntity<>(outputStage);
            ResponseEntity<OutputStage> response = restTemplate.exchange(outputStage.getUri(), HttpMethod.PUT, outputStageHttpEntity, OutputStage.class);
            if (response.getBody() == null) {
                throw new RestClientException("unexpected response body");
            }
            return response.getBody();
        } catch (GoodDataRestException | RestClientException e) {
            throw new GoodDataException("Unable to update output stage, uri: " + outputStage.getUri());
        }
    }
}
