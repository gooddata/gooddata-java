/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.Paging;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.List;
import java.util.Map;

/**
 * List of warehouses.
 */
@JsonDeserialize(using = WarehousesDeserializer.class)
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(Warehouses.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(using = WarehousesSerializer.class)
public class Warehouses extends Page<Warehouse> {

    public static final String URI = "/gdc/datawarehouse/instances";

    static final String ROOT_NODE = "instances";

    public Warehouses(final List<Warehouse> items, final Paging paging) {
        super(items, paging);
    }

    public Warehouses(final List<Warehouse> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }

}
