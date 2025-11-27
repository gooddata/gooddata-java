/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Map;

/**
 * Async task for warehouse. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("asyncTask")
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseTask {

    private static final String POLL_LINK = "poll";
    private static final String WAREHOUSE_LINK = "instance";
    private static final String WAREHOUSE_USER_LINK = "user";

    private final Map<String, String> links;

    @JsonCreator
    private WarehouseTask(@JsonProperty("links") Map<String, String> links) {
        this.links = links;
    }

    public String getPollUri() {
        return links.get(POLL_LINK);
    }

    public String getWarehouseUri() {
        return links.get(WAREHOUSE_LINK);
    }

    public String getWarehouseUserUri() {
        return links.get(WAREHOUSE_USER_LINK);
    }

}
