/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.hierarchicalconfig;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class ConfigItemTest {

    @Test
    public void testCustomValue() {
        final ConfigItem flag = new ConfigItem("test", "false");
        assertThat(flag.getValue(), is("false"));
        assertThat(flag.getKey(), is("test"));
    }

    @Test
    public void testConfigItemConversionMethodsAndGetUri() {
        final ConfigItem item = readObjectFromResource(getClass(), "/hierarchicalconfig/configItem.json",
                ConfigItem.class);

        assertThat(item.getSourceType(), is(SourceType.CATALOG));
        assertThat(item.isEnabled(), is(true));
        assertThat(item.getUri(), is("/gdc/projects/PROJECT_ID/config/myCoolFeature"));
    }

    @Test
    void testEmptyValues() {
        assertThrows(IllegalArgumentException.class, () -> new ConfigItem("", ""));
    }


    @Test
    void testEmptyNameWithValues() { 
        assertThrows(IllegalArgumentException.class, () -> new ConfigItem(" ", "false")); 
    }

    @Test
    public void testToStringFormat() {
        final ConfigItem item = new ConfigItem("test", "false");

        assertThat(item.toString(), matchesPattern(ConfigItem.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void shouldVerifyEquals() {
        EqualsVerifier.forClass(ConfigItem.class)
                .usingGetClass()
                .suppress(Warning.NONFINAL_FIELDS)
                .withIgnoredFields("links", "source")
                .verify();
    }
}
