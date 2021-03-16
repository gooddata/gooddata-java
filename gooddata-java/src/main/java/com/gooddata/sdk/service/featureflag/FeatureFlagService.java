/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.featureflag;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.featureflag.FeatureFlags;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlag;
import com.gooddata.sdk.model.featureflag.ProjectFeatureFlags;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItem;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.hierarchicalconfig.HierarchicalConfigService;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Provides feature flag management. Feature flag is a boolean flag used for enabling / disabling some specific feature
 * of GoodData platform. It can be used in various scopes (per project, per project group, per user, global etc.).
 * @deprecated Use {@link HierarchicalConfigService}.
 */
@Deprecated
public class FeatureFlagService extends AbstractService {

    public static final UriTemplate PROJECT_FEATURE_FLAG_TEMPLATE = new UriTemplate(ProjectFeatureFlag.PROJECT_FEATURE_FLAG_URI);
    public static final UriTemplate PROJECT_FEATURE_FLAGS_TEMPLATE = new UriTemplate(ProjectFeatureFlags.PROJECT_FEATURE_FLAGS_URI);
    public static final UriTemplate AGGREGATED_FEATURE_FLAGS_TEMPLATE = new UriTemplate(FeatureFlags.AGGREGATED_FEATURE_FLAGS_URI);

    /**
     * Constructs service for GoodData feature flags management.
     * @param restTemplate RESTful HTTP Spring template
     * @param settings settings
     */
    public FeatureFlagService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Returns aggregated feature flags for given project and current user (aggregates global, project group, project
     * and user feature flags).
     * It doesn't matter whether feature flag is enabled or not, it'll be included in both cases.
     *
     * @param project project, cannot be null
     * @return aggregated feature flags for given project and current user
     * @deprecated Use {@link HierarchicalConfigService#listProjectConfigItems(Project)}.
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
     * @deprecated Use {@link HierarchicalConfigService#listProjectConfigItems(Project)}.
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
     * Returns project feature flag (only project scoped flags, use {@link #listFeatureFlags(Project)} for aggregated
     * flags from all scopes) for given project by its unique name (aka "key").
     * It doesn't matter whether feature flag is enabled or not, it'll be included in both cases.
     *
     * @param project project, cannot be null
     * @param featureFlagName unique name (key) of feature flag, cannot be empty
     * @return feature flag for given project with given name (key)
     * @deprecated Use {@link HierarchicalConfigService#getProjectConfigItem(Project, String)}.
     */
    public ProjectFeatureFlag getProjectFeatureFlag(final Project project, final String featureFlagName) {
        notNull(project, "project");
        notEmpty(featureFlagName, "featureFlagName");

        try {
            return getProjectFeatureFlag(getProjectFeatureFlagUri(project, featureFlagName));
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
     * @deprecated Use {@link HierarchicalConfigService#setProjectConfigItem(Project, ConfigItem)}.
     */
    public ProjectFeatureFlag createProjectFeatureFlag(final Project project, final ProjectFeatureFlag flag) {
        notNull(project, "project");
        notNull(flag, "flag");

        final String featureFlagsUri = PROJECT_FEATURE_FLAGS_TEMPLATE.expand(project.getId()).toString();

        try {
            final URI featureFlagUri = restTemplate.postForLocation(featureFlagsUri, flag);
            if (featureFlagUri == null) {
                throw new GoodDataException("URI of new project feature flag can't be null");
            }
            return getProjectFeatureFlag(featureFlagUri.toString());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to create project feature flag: " + flag, e);
        }
    }

    /**
     * Updates existing project feature flag.
     * Note that it doesn't make sense to update any other property than {@link ProjectFeatureFlag#isEnabled()}.
     *
     * @param flag feature flag to be updated, cannot be null and it has to contain URI
     * @return updated feature flag
     * @deprecated Use {@link HierarchicalConfigService#setProjectConfigItem(Project, ConfigItem)}.
     */
    public ProjectFeatureFlag updateProjectFeatureFlag(final ProjectFeatureFlag flag) {
        notNull(flag, "flag");
        notEmpty(flag.getUri(), "flag.uri");

        try {
            restTemplate.put(flag.getUri(), flag);
            return getProjectFeatureFlag(flag.getUri());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to update project feature flag: " + flag, e);
        }
    }

    /**
     * Deletes existing project feature flag.
     *
     * @param flag existing project feature flag with links set properly, cannot be null
     * @deprecated Use {@link HierarchicalConfigService#removeProjectConfigItem(ConfigItem)}.
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


    String getProjectFeatureFlagUri(final Project project, final String flagName) {
        return PROJECT_FEATURE_FLAG_TEMPLATE.expand(project.getId(), flagName).toString();
    }

    private ProjectFeatureFlag getProjectFeatureFlag(final String flagUri) {
        final ProjectFeatureFlag result = restTemplate.getForObject(flagUri, ProjectFeatureFlag.class);
        if (result == null) {
            throw new GoodDataException("Project feature flag cannot be retrieved from URI " + flagUri);
        }
        return result;
    }

}
