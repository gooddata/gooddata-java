/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Map;

import static com.gooddata.sdk.common.util.Validate.notNullState;

/**
 * Warehouse schema.
 * Deserialization Only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("schema")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseSchema {

    private static final String SELF_LINK = "self";
    private static final String INSTANCE_LINK = "instance";

    public static final String URI = WarehouseSchemas.URI + "/{name}";

    private final String name;

    private final String description;

    private final Map<String, String> links;

    @JsonCreator
    private WarehouseSchema(@JsonProperty("name") final String name,
                            @JsonProperty("description") final String description,
                            @JsonProperty("links") final Map<String, String> links) {
        this.name = name;
        this.description = description;
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getInstanceUri() {
        return notNullState(links, "links").get(INSTANCE_LINK);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

