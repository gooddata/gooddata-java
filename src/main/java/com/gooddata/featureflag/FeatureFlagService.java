/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static com.gooddata.featureflag.FeatureFlags.AGGREGATED_FEATURE_FLAGS_TEMPLATE;
import static com.gooddata.util.Validate.notNull;

/**
 * Provides feature flag management. Feature flag is a boolean flag used for enabling / disabling some specific feature
 * of GoodData platform. It can be used in various scopes (per project, per project group, per user, global etc.).
 */
public class FeatureFlagService extends AbstractService {

    /**
     * Constructs service for GoodData feature flags management.
     *
     * @param restTemplate RESTful HTTP Spring template
     */
    public FeatureFlagService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Returns aggregated feature flags for given project and current user (aggregates global, project group, project
     * and user feature flags).
     * It doesn't matter whether feature flag is enabled or not, it'll be included in both cases.
     *
     * @param project project, cannot be null
     * @return aggregated feature flags for given project and current user
     */
    public FeatureFlags listFeatureFlags(final Project project) {
        notNull(project, "project");
        try {
            final FeatureFlags featureFlags = restTemplate
                    .getForObject(AGGREGATED_FEATURE_FLAGS_TEMPLATE.expand(project.getId()), FeatureFlags.class);

            if (featureFlags == null) {
                throw new GoodDataException("empty response from API call");
            }
            return featureFlags;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list aggregated feature flags for project ID=" + project.getId(), e);
        }
    }

}
