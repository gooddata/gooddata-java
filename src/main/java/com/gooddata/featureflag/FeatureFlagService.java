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

import static com.gooddata.featureflag.FeatureFlags.AGGREGATED_FEATURE_FLAGS_URI;
import static com.gooddata.featureflag.ProjectFeatureFlag.PROJECT_FEATURE_FLAG_TEMPLATE;
import static com.gooddata.featureflag.ProjectFeatureFlags.PROJECT_FEATURE_FLAGS_URI;
import static com.gooddata.util.Validate.notEmpty;
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
                    .getForObject(AGGREGATED_FEATURE_FLAGS_URI, FeatureFlags.class, project.getId());

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
                    .getForObject(PROJECT_FEATURE_FLAGS_URI, ProjectFeatureFlags.class, project.getId());

            if (projectFeatureFlags == null) {
                throw new GoodDataException("empty response from API call");
            }

            return projectFeatureFlags;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list project feature flags for project ID=" + project.getId(), e);
        }
    }

    /**
     * Returns project feature flag (only project scoped flags, use {@link #listFeatureFlags(Project)} for aggregated
     * flags from all scopes) for given project by its unique name (aka "key").
     * It doesn't matter whether feature flag is enabled or not, it'll be included in both cases.
     *
     * @param project project, cannot be null
     * @param featureFlagName unique name (key) of feature flag, cannot be empty
     * @return feature flag for given project with given name (key)
     */
    public ProjectFeatureFlag getProjectFeatureFlag(final Project project, final String featureFlagName) {
        notNull(project, "project");
        notEmpty(featureFlagName, "featureFlagName");

        try {
            final ProjectFeatureFlag flag = getProjectFeatureFlag(getProjectFeatureFlagUri(project, featureFlagName));

            if (flag == null) {
                throw new GoodDataException("empty response from API call");
            }

            return flag;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get project feature flag: " + featureFlagName, e);
        }
    }

    /**
     * Creates new feature flag for given project.
     * <p>
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

        try {
            final URI featureFlagUri = restTemplate.postForLocation(PROJECT_FEATURE_FLAGS_URI, flag, project.getId());
            if (featureFlagUri == null) {
                throw new GoodDataException("URI of new project feature flag can't be null");
            }
            return getProjectFeatureFlag(featureFlagUri);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create project feature flag: " + flag, e);
        }
    }

    /**
     * Updates existing project feature flag.
     * Note that it doesn't make sense to update any other property than {@link ProjectFeatureFlag#enabled}.
     *
     * @param flag feature flag to be updated, cannot be null and it has to contain URI
     * @return updated feature flag
     */
    public ProjectFeatureFlag updateProjectFeatureFlag(final ProjectFeatureFlag flag) {
        notNull(flag, "flag");
        notEmpty(flag.getUri(), "flag.uri");

        try {
            restTemplate.put(flag.getUri(), flag);
            return getProjectFeatureFlag(expandUri(flag.getUri()));
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to update project feature flag: " + flag, e);
        }
    }

    /**
     * Deletes existing project feature flag.
     *
     * @param flag existing project feature flag with links set properly, cannot be null
     */
    public void deleteProjectFeatureFlag(final ProjectFeatureFlag flag) {
        notNull(flag, "flag");
        notEmpty(flag.getUri(), "flag URI");

        try {
            restTemplate.delete(flag.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to delete project feature flag: " + flag, e);
        }
    }


    URI getProjectFeatureFlagUri(final Project project, final String flagName) {
        return expandUri(PROJECT_FEATURE_FLAG_TEMPLATE, project.getId(), flagName);
    }

    private ProjectFeatureFlag getProjectFeatureFlag(final URI flagUri) {
        final ProjectFeatureFlag result = restTemplate.getForObject(flagUri, ProjectFeatureFlag.class);
        if (result == null) {
            throw new GoodDataException("Project feature flag cannot be retrieved from URI " + flagUri);
        }
        return result;
    }

}
