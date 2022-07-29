/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.hierarchicalconfig;

import com.gooddata.sdk.model.hierarchicalconfig.ConfigItem;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItems;
import com.gooddata.sdk.model.hierarchicalconfig.SourceType;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource;
import static com.gooddata.sdk.service.hierarchicalconfig.HierarchicalConfigService.PROJECT_CONFIG_ITEMS_TEMPLATE;
import static com.gooddata.sdk.service.hierarchicalconfig.HierarchicalConfigService.PROJECT_CONFIG_ITEM_TEMPLATE;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;

public class HierarchicalConfigServiceIT extends AbstractGoodDataIT {

    private HierarchicalConfigService service;
    private Project project;

    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String PROJECT_CONFIG_ITEMS_URI_STRING = PROJECT_CONFIG_ITEMS_TEMPLATE.expand(PROJECT_ID).toString();
    private static final String CONFIG_ITEM_NAME = "myCoolFeature";
    private static final String PROJECT_CONFIG_ITEM_URI_STRING = PROJECT_CONFIG_ITEM_TEMPLATE
            .expand(PROJECT_ID, CONFIG_ITEM_NAME).toString();

    @BeforeMethod
    public void setUp() throws Exception {
        service = gd.getHierarchicalConfigService();
        project = readObjectFromResource("/project/project.json", Project.class);
    }

    @Test
    public void listProjectConfigItemsShouldReturnProjectConfigItems() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_CONFIG_ITEMS_URI_STRING)
                .respond()
                .withBody(readStringFromResource("/hierarchicalconfig/configItems.json"))
                .withStatus(200);

        final ConfigItems configItems = service.listProjectConfigItems(project);

        assertThat(configItems, containsInAnyOrder(
                new ConfigItem("myCoolFeature", "true"),
                new ConfigItem("mySuperCoolFeature", "true"),
                new ConfigItem("mySuperSecretFeature", "false"),
                new ConfigItem("myNotBooleanSetting", "Disabled")));
    }

    @Test
    public void setProjectConfigItemShouldCreateAndReturnSameConfigItem() {
        final String configItemUriString = PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), CONFIG_ITEM_NAME).toString();

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(PROJECT_CONFIG_ITEM_URI_STRING)
                .respond()
                .withStatus(204);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(configItemUriString)
                .respond()
                .withBody(readStringFromResource("/hierarchicalconfig/configItem.json"))
                .withStatus(200);

        final ConfigItem item = readObjectFromResource("/hierarchicalconfig/configItem.json", ConfigItem.class);
        final ConfigItem configItem = service.setProjectConfigItem(project, item);

        assertThat(configItem, is(notNullValue()));
        assertThat(configItem.getName(), is(CONFIG_ITEM_NAME));
        assertThat(configItem.isEnabled(), is(true));
        assertThat(configItem.getSource(), is("project"));
        assertThat(configItem.getSourceType(), is(SourceType.PROJECT));
        assertThat(configItem.getUri(), is("/gdc/projects/PROJECT_ID/config/myCoolFeature"));
    }

    @Test
    public void getConfigItemShouldReturnConfigItem() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(PROJECT_CONFIG_ITEM_URI_STRING)
                .respond()
                .withBody(readStringFromResource("/hierarchicalconfig/configItem.json"))
                .withStatus(200);

        final ConfigItem item = service.getProjectConfigItem(project, CONFIG_ITEM_NAME);

        assertThat(item, is(new ConfigItem(CONFIG_ITEM_NAME, "true")));
    }

    @Test
    public void removeProjectConfigItemShouldRemoveIt() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(PROJECT_CONFIG_ITEM_URI_STRING)
                .respond()
                .withStatus(204);

        final ConfigItem item = readObjectFromResource("/hierarchicalconfig/configItem.json", ConfigItem.class);
        service.removeProjectConfigItem(item);
    }
}
