/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.hierarchicalconfig;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.model.hierarchicalconfig.SourceType.*;

/**
 * Contains collection of config items aka feature flags aka hierarchical config.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("settings")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigItems implements Iterable<ConfigItem> {

    public static final String CLIENT_CONFIG_ITEMS_URI = CLIENT.getApiUri();
    public static final String DATA_PRODUCT_CONFIG_ITEMS_URI = DATA_PRODUCT.getApiUri();
    public static final String SEGMENT_CONFIG_ITEMS_URI = SEGMENT.getApiUri();
    public static final String DOMAIN_CONFIG_ITEMS_URI = DOMAIN.getApiUri();
    public static final String PROJECT_CONFIG_ITEMS_URI = PROJECT.getApiUri();
    public static final String PROJECT_GROUP_CONFIG_ITEMS_URI = PROJECT_GROUP.getApiUri();

    @JsonProperty("items")
    private final List<ConfigItem> items = new ArrayList<>();

    @JsonCreator
    public ConfigItems(@JsonProperty("items") List<ConfigItem> items) {
        if (items != null && !items.isEmpty()) {
            this.items.addAll(items);
        }
    }

    @Override
    public Iterator<ConfigItem> iterator() {
        return items.iterator();
    }

    /**
     * Returns value if config item exists, otherwise returns null.
     *
     * @param configName the name/key of the config item
     * @return value if config item with given name/key exists, null otherwise
     */
    public String getValue(final String configName) {
        notEmpty(configName, "configName");
        for (final ConfigItem item : items) {
            if (configName.equalsIgnoreCase(item.getKey())) {
                return item.getValue();
            }
        }
        return null;
    }

    /**
     * Returns true if the config item with given name/key exists and is enabled, false otherwise.
     *
     * @param configName the name/key of config item
     * @return true if the config item with given name/key exists and is enabled, false otherwise
     */
    public boolean isEnabled(final String configName) {
        notEmpty(configName, "configName");
        for (final ConfigItem item : items) {
            if (configName.equalsIgnoreCase(item.getKey())) {
                return Boolean.parseBoolean(item.getValue());
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
