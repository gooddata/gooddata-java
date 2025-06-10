/*
 * (C) 2023 GoodData Corporation.
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

import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
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

    public HierarchicalConfigService(WebClient webClient, GoodDataSettings settings) { 
        super(webClient, settings);
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
            ConfigItems configItems = webClient.get() 
                .uri(PROJECT_CONFIG_ITEMS_TEMPLATE.expand(project.getId()))
                .retrieve()
                .bodyToMono(ConfigItems.class)
                .block(); 

            if (configItems == null) {
                throw new GoodDataException("empty response from API call");
            }
            return configItems;
        } catch (WebClientResponseException | GoodDataException e) { 
            throw new GoodDataException("Unable to list config items for project ID=" + project.getId(), e);
        } catch (Exception e) {
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
        } catch (WebClientResponseException | GoodDataException e) {   
            throw new GoodDataException("Unable to get project config item: " + configName, e);
        } catch (Exception e) {
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
            webClient.put()  
                .uri(configUri)
                .bodyValue(configItem)
                .retrieve()
                .toBodilessEntity()
                .block();  
            return getProjectConfigItem(configUri);  
        } catch (WebClientResponseException | GoodDataException e) {  
            throw new GoodDataException("Unable to set project config item: " + configItem.getKey(), e);
        }
    }

    /**
     * Removes existing project config item.
     *
     * @param configItem existing project config item with links set properly, cannot be null
     */
    public void removeProjectConfigItem(ConfigItem configItem) {
        notNull(configItem, "configItem");
        try {
            webClient.delete()
                .uri(configItem.getUri())
                .retrieve()
                .toBodilessEntity()
                .block();
        } catch (Exception e) {
            throw new GoodDataException("Unable to remove project config item: " + configItem.getUri(), e);
        }
    }


    private ConfigItem getProjectConfigItem(String configUri) {
        ConfigItem configItem = webClient.get()  
            .uri(configUri)
            .retrieve()
            .bodyToMono(ConfigItem.class)
            .block();  

        if (configItem == null) {
            throw new GoodDataException("Project config item cannot be retrieved from URI " + configUri);
        }
        return configItem;
    }
}
