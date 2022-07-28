/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.warehouse;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;

/**
 * Warehouse schema doesn't exist.
 */
public class WarehouseSchemaNotFoundException extends GoodDataException {

    private final String warehouseSchemaUri;

    public WarehouseSchemaNotFoundException(String uri, GoodDataRestException cause) {
        super("Warehouse schema " + uri + " was not found", cause);
        this.warehouseSchemaUri = uri;
    }

    public String getWarehouseSchemaUri() {
        return warehouseSchemaUri;
    }
}
