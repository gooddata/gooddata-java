/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataload;

import static com.gooddata.util.Validate.isTrue;
import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.project.Project;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Service to manage output stage.
 */
public class OutputStageService extends AbstractService {

    /**
     * Sets RESTful HTTP Spring template. Should be called from constructor of concrete service extending
     * this abstract one.
     * @param restTemplate RESTful HTTP Spring template
     */
    public OutputStageService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Get output stage by given URI.
     * @param uri output stage uri
     * @return output stage object
     * @throws com.gooddata.dataload.processes.ProcessNotFoundException when the process doesn't exist
     */
    public OutputStage getOutputStageByUri(final String uri) {
        notEmpty(uri, "uri");
        isTrue(OutputStage.TEMPLATE.matches(uri), "uri does not match output stage pattern: " + OutputStage.TEMPLATE.toString());
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
     * @throws com.gooddata.dataload.processes.ProcessNotFoundException when the process doesn't exist
     */
    public OutputStage getOutputStage(final Project project) {
        notNull(project, "project");
        return getOutputStageByUri(OutputStage.TEMPLATE.expand(project.getId()).toString());
    }

    /**
     * Update output stage.
     *
     * @param outputStage output stage
     * @return updated output stage
     */
    public OutputStage updateOutputStage(final OutputStage outputStage) {
        notNull(outputStage, "outputStage");
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
