/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.collections.PageableList;
import com.gooddata.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * List of warehouse schemas.
 * Deserialization Only.
 */
@JsonDeserialize(using = WarehouseSchemasDeserializer.class)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(WarehouseSchemas.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class WarehouseSchemas extends PageableList<WarehouseSchema> {

    public static final String URI = Warehouse.URI + "/schemas";

    static final String ROOT_NODE = "schemas";

    public WarehouseSchemas(final List<WarehouseSchema> items, final Paging paging) {
        super(items, paging);
    }

    public WarehouseSchemas(final List<WarehouseSchema> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }
}
