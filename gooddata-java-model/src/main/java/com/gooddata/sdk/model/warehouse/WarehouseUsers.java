/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * List of warehouse users.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName(WarehouseUsers.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = WarehouseUsersDeserializer.class)
@JsonSerialize(using = WarehouseUsersSerializer.class)
public class WarehouseUsers extends Page<WarehouseUser> {

    public static final String URI = Warehouse.URI + "/users";

    static final String ROOT_NODE = "users";

    public WarehouseUsers(final List<WarehouseUser> items, final Paging paging) {
        super(items, paging);
    }

    public WarehouseUsers(final List<WarehouseUser> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }

}
