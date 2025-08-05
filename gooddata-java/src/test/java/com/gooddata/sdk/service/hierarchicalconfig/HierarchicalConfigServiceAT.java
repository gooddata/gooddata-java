/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.hierarchicalconfig;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItem;
import com.gooddata.sdk.model.hierarchicalconfig.ConfigItems;
import com.gooddata.sdk.model.hierarchicalconfig.SourceType;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.service.hierarchicalconfig.HierarchicalConfigService.PROJECT_CONFIG_ITEM_TEMPLATE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.fail;

public class HierarchicalConfigServiceAT extends AbstractGoodDataAT {

    private static final String FIRST_CONFIG_ITEM = "flag1";
    private static final String SECOND_CONFIG_ITEM = "flag2";

    @Test
    public void createProjectConfigItem() {
        final ConfigItem configItem = gd.getHierarchicalConfigService()
                .setProjectConfigItem(project, new ConfigItem(FIRST_CONFIG_ITEM, "true"));
        final ConfigItem secondConfigItem = gd.getHierarchicalConfigService()
                .setProjectConfigItem(project, new ConfigItem(SECOND_CONFIG_ITEM, "false"));

        checkConfigItem(configItem, FIRST_CONFIG_ITEM, "true",
                PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), FIRST_CONFIG_ITEM).toString()
        );
        checkConfigItem(secondConfigItem, SECOND_CONFIG_ITEM, "false",
                PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), SECOND_CONFIG_ITEM).toString()
        );
    }

    @Test
    public void listProjectConfigItems() {
        final ConfigItems configItems = gd.getHierarchicalConfigService().listProjectConfigItems(project);

        assertThat(configItems, hasItems(
                new ConfigItem(SECOND_CONFIG_ITEM, "false"),
                new ConfigItem(FIRST_CONFIG_ITEM, "true")));
    }

    @Test
    public void getProjectConfigItem() {
        final ConfigItem configItem =
                gd.getHierarchicalConfigService().getProjectConfigItem(project, SECOND_CONFIG_ITEM);
        checkConfigItem(configItem, SECOND_CONFIG_ITEM, "false",
                PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), SECOND_CONFIG_ITEM).toString()
        );
    }

    @Test
    public void updateProjectConfigItem() {
        final ConfigItem configItem = gd.getHierarchicalConfigService().getProjectConfigItem(project,
                FIRST_CONFIG_ITEM);

        // update config item value to disable it
        configItem.setValue("false");
        final ConfigItem disabledConfigItem = gd.getHierarchicalConfigService().setProjectConfigItem(project, configItem);
        checkConfigItem(disabledConfigItem, FIRST_CONFIG_ITEM, "false",
                PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), FIRST_CONFIG_ITEM).toString()
        );

        // enable again
        configItem.setValue("true");
        final ConfigItem enabledConfigItem = gd.getHierarchicalConfigService().setProjectConfigItem(project, configItem);
        checkConfigItem(enabledConfigItem, FIRST_CONFIG_ITEM, "true",
                PROJECT_CONFIG_ITEM_TEMPLATE.expand(project.getId(), FIRST_CONFIG_ITEM).toString()
        );
    }

    @Test
    public void removeProjectConfigItem() {
        ConfigItem flagItem = gd.getHierarchicalConfigService().getProjectConfigItem(project, FIRST_CONFIG_ITEM);

        gd.getHierarchicalConfigService().removeProjectConfigItem(flagItem);
        try {
            gd.getHierarchicalConfigService().getProjectConfigItem(project, FIRST_CONFIG_ITEM);
            fail();
        } catch (GoodDataException e) {
            assertThat(e.getMessage(), containsString("Unable to get project config item: " + FIRST_CONFIG_ITEM));
        }
    }

    private void checkConfigItem(final ConfigItem item, final String expectedKey,
                                 final String expectedValue, final String uri) {
        assertThat(item, is(notNullValue()));
        assertThat(item.getKey(), is(expectedKey));
        assertThat(item.getValue(), is(expectedValue));
        assertThat(item.getSourceType(), is(SourceType.PROJECT));
        assertThat(item.getSource(), is("project"));
        assertThat(item.getUri(), is(uri));
    }

}
