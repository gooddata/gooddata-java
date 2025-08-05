/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.dataload.OutputStage;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import static com.gooddata.sdk.common.util.Validate.*;

/**
 * Service to manage output stage.
 */
public class OutputStageService extends AbstractService {

    public static final UriTemplate OUTPUT_STAGE_TEMPLATE = new UriTemplate(OutputStage.URI);

    /**
     * Constructor accepting WebClient and settings.
     * @param webClient WebClient for HTTP communication
     * @param settings configuration settings
     */
    public OutputStageService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings);
    }

    /**
     * Get output stage by a given URI.
     * @param uri output stage URI
     * @return output stage object
     * @throws GoodDataException if the output stage does not exist or an error occurs
     */
    public OutputStage getOutputStageByUri(final String uri) {
        notEmpty(uri, "uri");
        isTrue(OUTPUT_STAGE_TEMPLATE.matches(uri), "uri does not match output stage pattern: " + OUTPUT_STAGE_TEMPLATE.toString());
        try {
            OutputStage outputStage = webClient.get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(OutputStage.class)
                    .block();

            if (outputStage == null) {
                throw new GoodDataException("OutputStage not found: " + uri);
            }
            return outputStage;
        } catch (Exception e) {
            throw new GoodDataException("Unable to get output stage " + uri, e);
        }
    }

    /**
     * Get output stage by project.
     * @param project project for which to get the output stage
     * @return output stage object
     * @throws GoodDataException if the output stage does not exist or an error occurs
     */
    public OutputStage getOutputStage(final Project project) {
        notNull(project, "project");
        notNull(project.getId(), "project.id");

        return getOutputStageByUri(OUTPUT_STAGE_TEMPLATE.expand(project.getId()).toString());
    }

    /**
     * Update the output stage.
     * @param outputStage output stage to update
     * @return updated output stage
     * @throws GoodDataException if update fails or the response is empty
     */
    public OutputStage updateOutputStage(final OutputStage outputStage) {
        notNull(outputStage, "outputStage");
        notNull(outputStage.getUri(), "outputStage.uri");

        try {
            OutputStage updated = webClient.put()
                    .uri(outputStage.getUri())
                    .bodyValue(outputStage)
                    .retrieve()
                    .bodyToMono(OutputStage.class)
                    .block();

            if (updated == null) {
                throw new GoodDataException("Unexpected empty response body");
            }
            return updated;
        } catch (Exception e) {
            throw new GoodDataException("Unable to update output stage, uri: " + outputStage.getUri(), e);
        }
    }
}
