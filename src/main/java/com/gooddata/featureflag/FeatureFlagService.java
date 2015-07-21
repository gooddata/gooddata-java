/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.featureflag;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.project.Project;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

import static com.gooddata.featureflag.FeatureFlags.AGGREGATED_FEATURE_FLAGS_TEMPLATE;
import static com.gooddata.featureflag.ProjectFeatureFlags.PROJECT_FEATURE_FLAGS_TEMPLATE;
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

    /**
     * Returns project feature flags (only project scoped flags, use {@link #listFeatureFlags(Project)} for aggregated
     * flags from all scopes) for given project.
     * It doesn't matter whether feature flag is enabled or not, it'll be included in both cases.
     *
     * @param project project, cannot be null
     * @return list of all feature flags for given project
     */
    public ProjectFeatureFlags listProjectFeatureFlags(final Project project) {
        notNull(project, "project");
        try {
            final ProjectFeatureFlags projectFeatureFlags = restTemplate
                    .getForObject(PROJECT_FEATURE_FLAGS_TEMPLATE.expand(project.getId()), ProjectFeatureFlags.class);

            if (projectFeatureFlags == null) {
                throw new GoodDataException("empty response from API call");
            }

            return projectFeatureFlags;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list project feature flags for project ID=" + project.getId(), e);
        }
    }

    /**
     * Creates new feature flag for given project.
     * <p/>
     * Usually, it doesn't make sense to create feature flag that is disabled because
     * this is the same as having no feature flag at all.
     *
     * @param project project for which the feature flag should be created, cannot be null
     * @param flag feature flag to be created, cannot be null
     * @return created feature flag
     */
    public ProjectFeatureFlag createProjectFeatureFlag(final Project project, final ProjectFeatureFlag flag) {
        notNull(project, "project");
        notNull(flag, "flag");

        final String featureFlagsUri = PROJECT_FEATURE_FLAGS_TEMPLATE.expand(project.getId()).toString();

        try {
            final URI featureFlagUri = restTemplate.postForLocation(featureFlagsUri, flag);
            if (featureFlagUri == null) {
                throw new GoodDataException("URI of new flag can't be null");
            }
            return getProjectFeatureFlag(featureFlagUri.toString());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create feature flag: " + flag, e);
        }
    }


    private ProjectFeatureFlag getProjectFeatureFlag(final String flagUri) {
        final ProjectFeatureFlag result = restTemplate.getForObject(flagUri, ProjectFeatureFlag.class);
        if (result == null) {
            throw new GoodDataException("flag cannot be retrieved");
        }
        return result;
    }

}
