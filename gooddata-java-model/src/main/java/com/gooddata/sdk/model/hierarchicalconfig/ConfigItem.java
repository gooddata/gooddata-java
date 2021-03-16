/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.hierarchicalconfig;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static com.gooddata.sdk.model.hierarchicalconfig.SourceType.*;

/**
 * Contains information about hierarchical configuration object aka platform setting aka feature flag.
 * Purpose of config item is carrying information about some specific feature of Gooddata platform.
 */
@JsonTypeName("settingItem")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigItem {

    private static final String CONFIG_NAME_SUFFIX = "/{configName}";
    public static final String CLIENT_CONFIG_ITEM_URI = CLIENT.getApiUri() + CONFIG_NAME_SUFFIX;
    public static final String DATA_PRODUCT_CONFIG_ITEM_URI = DATA_PRODUCT.getApiUri() + CONFIG_NAME_SUFFIX;
    public static final String SEGMENT_CONFIG_ITEM_URI = SEGMENT.getApiUri() + CONFIG_NAME_SUFFIX;
    public static final String DOMAIN_CONFIG_ITEM_URI = DOMAIN.getApiUri() + CONFIG_NAME_SUFFIX;
    public static final String PROJECT_CONFIG_ITEM_URI = PROJECT.getApiUri() + CONFIG_NAME_SUFFIX;
    public static final String PROJECT_GROUP_CONFIG_ITEM_URI = PROJECT_GROUP.getApiUri() + CONFIG_NAME_SUFFIX;

    private final String key;
    private String value;
    private final String source;
    private final Links links;

    /**
     * Creates new config item with given key/name and value.
     *
     * @param key unique key/name of config item
     * @param value value of config item
     */
    public ConfigItem(String key, String value) {
        this(notEmpty(key, "key"), notEmpty(value, "value"), null, null);
    }

    @JsonCreator
    ConfigItem(@JsonProperty("key") String key, @JsonProperty("value") String value,
               @JsonProperty("source") String source, @JsonProperty("links") Links links) {
        this.key = key;
        this.value = value;
        this.source = source;
        this.links = links;
    }

    /**
     * Get unique key/name of config item. The same as {@link #getName()}.
     */
    @JsonProperty("key")
    public String getKey() {
        return key;
    }

    /**
     * Get unique key/name of config item. The same as {@link #getKey()}.
     */
    @JsonIgnore
    public String getName() {
        return key;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    @JsonIgnore
    public String getSource() {
        return source;
    }

    @JsonIgnore
    public Links getLinks() {
        return links;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Conversion method from String to Boolean type.
     * @return returns converted value: true or false
     */
    @JsonIgnore
    public boolean isEnabled() {
        return Boolean.parseBoolean(this.value);
    }

    public void setEnabled(Boolean value) {
        this.value = String.valueOf(value);
    }

    /**
     * Conversion method from source to SourceType enum.
     * @return returns SourceType or null, if source is not recognized
     */
    @JsonIgnore
    public SourceType getSourceType() {
        return SourceType.get(source);
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").getSelf();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ConfigItem that = (ConfigItem) o;

        if (value != null ? !value.equals(that.value) : that.value != null) {
            return false;
        }
        return !(key != null ? !key.equals(that.key) : that.key != null);

    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Links {
        private final String self;

        @JsonCreator
        public Links(@JsonProperty("self") String self) {
            this.self = self;
        }

        public String getSelf() {
            return self;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

}
