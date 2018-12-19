/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import static com.gooddata.util.Validate.notNullState;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

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
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

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
