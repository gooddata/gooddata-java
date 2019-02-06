/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

/**
 * Role of the user in the Datawarehouse
 */
public enum WarehouseUserRole {
    ADMIN("admin"),
    DATA_ADMIN("dataAdmin"),
    READ_ONLY("readOnly");

    private final String roleName;

    WarehouseUserRole(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }
}
