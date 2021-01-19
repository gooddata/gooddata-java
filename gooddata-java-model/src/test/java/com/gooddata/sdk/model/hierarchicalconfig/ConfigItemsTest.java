/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.hierarchicalconfig;

import org.testng.annotations.Test;

import java.io.IOException;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.oneOf;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.testng.AssertJUnit.assertNotNull;

public class ConfigItemsTest {

    @Test
    public void testDeserialize() {
        final ConfigItems configItems = readObjectFromResource(getClass(), "/hierarchicalconfig/configItems.json",
                ConfigItems.class);
        assertNotNull(configItems);

        assertThat(configItems, containsInAnyOrder(
                new ConfigItem("myCoolFeature", "true"),
                new ConfigItem("mySuperCoolFeature", "true"),
                new ConfigItem("mySuperSecretFeature", "false")));
    }

    @Test
    public void testSerialize() throws IOException {
        final ConfigItems projectFeatureFlags = new ConfigItems(asList(
                new ConfigItem("enabledSettingItem", "true"),
                new ConfigItem("disabledSettingItem", "false")));

        final String serializedConfigItem = OBJECT_MAPPER.writeValueAsString(projectFeatureFlags);

        assertThat(serializedConfigItem, startsWith("{\"settings\""));
        assertThat(serializedConfigItem, containsString("\"key\":\"enabledSettingItem\",\"value\":\"true\""));
        assertThat(serializedConfigItem, containsString("\"key\":\"disabledSettingItem\",\"value\":\"false\""));
    }

    @Test
    public void shouldIterateThroughItemsInForeach() {
        final ConfigItems items = readObjectFromResource(getClass(), "/hierarchicalconfig/configItems.json",
                ConfigItems.class);
        for (ConfigItem item : items) {
            assertThat(item, is(oneOf(
                    new ConfigItem("myCoolFeature", "true"),
                    new ConfigItem("mySuperCoolFeature", "true"),
                    new ConfigItem("mySuperSecretFeature", "false"))));
        }
    }

    @Test
    public void isEnabledShouldReturnCorrectBoolean() {
        final ConfigItems items = new ConfigItems(asList(
                new ConfigItem("enabledItem", "true"),
                new ConfigItem("disabledItem", "false")
        ));

        assertThat(items.isEnabled("enabledItem"), is(true));
        assertThat(items.isEnabled("disabledItem"), is(false));
        assertThat(items.isEnabled("nonexistentItem"), is(false));
    }

    @Test
    public void getValueShouldReturnValueOrNull() {
        final ConfigItems items = new ConfigItems(asList(
                new ConfigItem("enabledItem", "true"),
                new ConfigItem("disabledItem", "false")
        ));

        assertThat(items.getValue("enabledItem"), is("true"));
        assertThat(items.getValue("disabledItem"), is("false"));
        assertThat(items.getValue("nonexistentItem"), nullValue());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullNameThenIsEnabledShouldThrow() {
        final ConfigItems items = new ConfigItems(
                singletonList(new ConfigItem("enabledItem", "true")));
        items.isEnabled(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void whenNullNameThenGetValueShouldThrow() {
        final ConfigItems items = new ConfigItems(
                singletonList(new ConfigItem("enabledItem", "true")));
        items.getValue(null);
    }

    @Test
    public void whenNullArgumentInConstructorThenShouldStillWork() {
        final ConfigItems items = new ConfigItems(null);
        assertNotNull(items.iterator());
        assertThat(items.isEnabled("nonexistentFlag"), is(false));
    }
}
