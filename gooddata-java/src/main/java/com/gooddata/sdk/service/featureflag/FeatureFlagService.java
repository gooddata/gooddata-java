/*
 * (C) 2023 GoodData Corporation.
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
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Provides feature flag management. Feature flag is a boolean flag used for enabling/disabling some specific feature
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
     * @param webClient WebClient for HTTP communication
     * @param settings settings
     */
    public FeatureFlagService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings);
    }

    /**
     * Returns aggregated feature flags for given project and current user.
     * @deprecated Use {@link HierarchicalConfigService#listProjectConfigItems(Project)}.
     */
    public FeatureFlags listFeatureFlags(final Project project) {
        notNull(project, "project");
        try {
            FeatureFlags featureFlags = webClient.get()
                    .uri(AGGREGATED_FEATURE_FLAGS_TEMPLATE.expand(project.getId()).toString())
                    .retrieve()
                    .bodyToMono(FeatureFlags.class)
                    .block();

            if (featureFlags == null) {
                throw new GoodDataException("Empty response from API call");
            }
            return featureFlags;
        } catch (Exception e) {
            throw new GoodDataException("Unable to list aggregated feature flags for project ID=" + project.getId(), e);
        }
    }

    /**
     * Returns project feature flags for given project.
     * @deprecated Use {@link HierarchicalConfigService#listProjectConfigItems(Project)}.
     */
    public ProjectFeatureFlags listProjectFeatureFlags(final Project project) {
        notNull(project, "project");
        try {
            ProjectFeatureFlags projectFeatureFlags = webClient.get()
                    .uri(PROJECT_FEATURE_FLAGS_TEMPLATE.expand(project.getId()).toString())
                    .retrieve()
                    .bodyToMono(ProjectFeatureFlags.class)
                    .block();

            if (projectFeatureFlags == null) {
                throw new GoodDataException("Empty response from API call");
            }

            return projectFeatureFlags;
        } catch (Exception e) {
            throw new GoodDataException("Unable to list project feature flags for project ID=" + project.getId(), e);
        }
    }

    /**
     * Returns project feature flag for given project by its unique name (key).
     * @deprecated Use {@link HierarchicalConfigService#getProjectConfigItem(Project, String)}.
     */
    public ProjectFeatureFlag getProjectFeatureFlag(final Project project, final String featureFlagName) {
        notNull(project, "project");
        notEmpty(featureFlagName, "featureFlagName");

        try {
            return getProjectFeatureFlag(getProjectFeatureFlagUri(project, featureFlagName));
        } catch (Exception e) {
            throw new GoodDataException("Unable to get project feature flag: " + featureFlagName, e);
        }
    }

    /**
     * Creates new feature flag for given project.
     * @deprecated Use {@link HierarchicalConfigService#setProjectConfigItem(Project, ConfigItem)}.
     */
    public ProjectFeatureFlag createProjectFeatureFlag(final Project project, final ProjectFeatureFlag flag) {
        notNull(project, "project");
        notNull(flag, "flag");

        final String featureFlagsUri = PROJECT_FEATURE_FLAGS_TEMPLATE.expand(project.getId()).toString();

        try {
            URI featureFlagUri = webClient.post()
                    .uri(featureFlagsUri)
                    .bodyValue(flag)
                    .retrieve()
                    .toBodilessEntity()
                    .map(response -> response.getHeaders().getLocation())
                    .block();

            if (featureFlagUri == null) {
                throw new GoodDataException("URI of new project feature flag can't be null");
            }
            return getProjectFeatureFlag(featureFlagUri.toString());
        } catch (Exception e) {
            throw new GoodDataException("Unable to create project feature flag: " + flag, e);
        }
    }

    /**
     * Updates existing project feature flag.
     * @deprecated Use {@link HierarchicalConfigService#setProjectConfigItem(Project, ConfigItem)}.
     */
    public ProjectFeatureFlag updateProjectFeatureFlag(final ProjectFeatureFlag flag) {
        notNull(flag, "flag");
        notEmpty(flag.getUri(), "flag.uri");

        try {
            webClient.put()
                    .uri(flag.getUri())
                    .bodyValue(flag)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return getProjectFeatureFlag(flag.getUri());
        } catch (Exception e) {
            throw new GoodDataException("Unable to update project feature flag: " + flag, e);
        }
    }

    /**
     * Deletes existing project feature flag.
     * @deprecated Use {@link HierarchicalConfigService#removeProjectConfigItem(ConfigItem)}.
     */
    public void deleteProjectFeatureFlag(final ProjectFeatureFlag flag) {
        notNull(flag, "flag");
        notEmpty(flag.getUri(), "flag URI");

        try {
            webClient.delete()
                    .uri(flag.getUri())
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to delete project feature flag: " + flag, e);
        }
    }

    String getProjectFeatureFlagUri(final Project project, final String flagName) {
        return PROJECT_FEATURE_FLAG_TEMPLATE.expand(project.getId(), flagName).toString();
    }

    private ProjectFeatureFlag getProjectFeatureFlag(final String flagUri) {
        ProjectFeatureFlag result = webClient.get()
                .uri(flagUri)
                .retrieve()
                .bodyToMono(ProjectFeatureFlag.class)
                .block();

        if (result == null) {
            throw new GoodDataException("Project feature flag cannot be retrieved from URI " + flagUri);
        }
        return result;
    }

}
