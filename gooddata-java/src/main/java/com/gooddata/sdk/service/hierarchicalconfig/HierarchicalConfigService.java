/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.hierarchicalconfig;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItem;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItems;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractService;
import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Provides hierarchical configuration management a.k.a. platform settings a.k.a. feature flags.
 * For more detailed description, see this documentation https://help.gooddata.com/api#/reference/hierarchical-configuration
 */
public class HierarchicalConfigService extends AbstractService {

    public final static UriTemplate PROJECT_CONFIG_ITEMS_TEMPLATE = new UriTemplate(ConfigItems.PROJECT_CONFIG_ITEMS_URI);
    public final static UriTemplate PROJECT_CONFIG_ITEM_TEMPLATE = new UriTemplate(ConfigItem.PROJECT_CONFIG_ITEM_URI);

    public HierarchicalConfigService(RestTemplate restTemplate, GoodDataSettings settings) {
        super(restTemplate, settings);
    }

    /**
     * Returns all config items for given project (including inherited ones from its hierarchy).
     *
     * @param project project, cannot be null
     * @return config item for given project
     */
    public ConfigItems listProjectConfigItems(final Project project) {
        notNull(project, "project");
        try {
            ConfigItems configItems = restTemplate.getForObject(PROJECT_CONFIG_ITEMS_TEMPLATE.expand(project.getId()), ConfigItems.class);

            if (configItems == null) {
                throw new GoodDataException("empty response from API call");
            }
            return configItems;
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to list config items for project ID=" + project.getId(), e);
        }
    }

    /**
     * Returns config item for given project (even if it's inherited from its hierarchy).
     *
     * @param project project, cannot be null
     * @param configName unique name (key) of config item, cannot be empty
     * @return config item for given project with given name (key)
     */
    public ConfigItem getProjectConfigItem(final Project project, final String configName) {
        notNull(project, "project");
        notEmpty(configName, "configName");

        try {
            String configUri = PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), configName).toString();
            return getProjectConfigItem(configUri);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to get project config item: " + configName, e);
        }
    }

    /**
     * Creates or updates config item for given project.
     *
     * @param project project for which the config item should be created/updated, cannot be null
     * @param configItem config item to be created/updated, cannot be null
     * @return created/updated project config item
     */
    public ConfigItem setProjectConfigItem(final Project project, final ConfigItem configItem) {
        notNull(project, "project");
        notNull(configItem, "configItem");

        try {
            String configUri = PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), configItem.getName()).toString();
            restTemplate.put(configUri, configItem);
            return getProjectConfigItem(configUri);
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to set project config item: " + configItem.getKey(), e);
        }
    }

    /**
     * Removes existing project config item.
     *
     * @param configItem existing project config item with links set properly, cannot be null
     */
    public void removeProjectConfigItem(final ConfigItem configItem) {
        notNull(configItem, "configItem");
        notEmpty(configItem.getUri(), "config item URI");

        try {
            restTemplate.delete(configItem.getUri());
        } catch (RestClientException e) {
            throw new GoodDataException("Unable to remove project config item: " + configItem.getKey(), e);
        }
    }

    private ConfigItem getProjectConfigItem(String configUri) {
        ConfigItem configItem = restTemplate.getForObject(configUri, ConfigItem.class);

        if (configItem == null) {
            throw new GoodDataException("Project config item cannot be retrieved from URI " + configUri);
        }
        return configItem;
    }
}
