/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.hierarchicalconfig;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItem;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItems;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

public class HierarchicalConfigServiceTest {

    private static final String PROJECT_ID = "11";
    private static final String CONFIG_ITEM_NAME = "item1";
    private static final String PROJECT_CONFIG_ITEMS_URI = "/gdc/projects/11/config";
    private static final String PROJECT_CONFIG_ITEM_URI = "/gdc/projects/11/config/" + CONFIG_ITEM_NAME;

    @Mock
    private Project project;
    @Mock
    private ConfigItem configItem;
    @Mock
    private RestTemplate restTemplate;

    private HierarchicalConfigService hierarchicalConfigService;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        hierarchicalConfigService = new HierarchicalConfigService(restTemplate, new GoodDataSettings());
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    /*--- Project Config ---*/

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullArgThenListProjectConfigItemsShouldThrow() {
        hierarchicalConfigService.listProjectConfigItems(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenEmptyResponseThenListProjectConfigItemsShouldThrow() throws Exception {
        when(restTemplate.getForObject(new URI(PROJECT_CONFIG_ITEMS_URI), ConfigItems.class)).thenReturn(null);
        hierarchicalConfigService.listProjectConfigItems(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenClientErrorResponseThenListProjectConfigItemsShouldThrow() throws Exception {
        when(restTemplate.getForObject(new URI(PROJECT_CONFIG_ITEMS_URI), ConfigItems.class))
                .thenThrow(new RestClientException(""));
        hierarchicalConfigService.listProjectConfigItems(project);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullKeyThenGetProjectConfigItemShouldThrow() {
        hierarchicalConfigService.getProjectConfigItem(project,null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullProjectThenGetProjectConfigItemShouldThrow() {
        hierarchicalConfigService.getProjectConfigItem(null, CONFIG_ITEM_NAME);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenEmptyResponseThenGetProjectConfigItemShouldThrow() {
        when(restTemplate.getForObject(CONFIG_ITEM_NAME, ConfigItem.class)).thenReturn(null);
        hierarchicalConfigService.getProjectConfigItem(project, CONFIG_ITEM_NAME);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenClientErrorResponseThenGetProjectConfigItemShouldThrow() {
        when(restTemplate.getForObject(PROJECT_CONFIG_ITEM_URI, ConfigItem.class))
                .thenThrow(new RestClientException(""));
        hierarchicalConfigService.getProjectConfigItem(project, CONFIG_ITEM_NAME);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullConfigItemThenSetProjectConfigItemShouldThrow() {
        hierarchicalConfigService.setProjectConfigItem(project,null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullProjectThenSetProjectConfigItemShouldThrow() {
        hierarchicalConfigService.setProjectConfigItem(null, configItem);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenEmptyResponseThenSettProjectConfigItemShouldThrow() {
        when(configItem.getUri()).thenReturn(PROJECT_CONFIG_ITEM_URI);
        when(restTemplate.getForObject(PROJECT_CONFIG_ITEM_URI, ConfigItem.class)).thenReturn(null);
        hierarchicalConfigService.setProjectConfigItem(project, configItem);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenClientErrorResponseThenSetProjectConfigItemShouldThrow() {
        when(configItem.getUri()).thenReturn(PROJECT_CONFIG_ITEM_URI);
        when(restTemplate.getForObject(PROJECT_CONFIG_ITEM_URI, ConfigItem.class))
                .thenThrow(new RestClientException(""));
        hierarchicalConfigService.setProjectConfigItem(project, configItem);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullFlagThenRemoveProjectConfigItemShouldThrow() {
        hierarchicalConfigService.removeProjectConfigItem(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void whenClientErrorResponseThenRemoveProjectConfigItemShouldThrow() {
        when(configItem.getUri()).thenReturn(PROJECT_CONFIG_ITEM_URI);
        doThrow(new RestClientException("")).when(restTemplate).delete(PROJECT_CONFIG_ITEM_URI);
        hierarchicalConfigService.removeProjectConfigItem(configItem);
    }

}
