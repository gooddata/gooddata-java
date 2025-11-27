/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.warehouse;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;

/**
 * Warehouse instance doesn't exist.
 */
public class WarehouseNotFoundException extends GoodDataException {

    private final String warehouseUri;

    public WarehouseNotFoundException(String uri, GoodDataRestException cause) {
        super("Warehouse instance " + uri + " was not found", cause);
        this.warehouseUri = uri;
    }

    public String getWarehouseUri() {
        return warehouseUri;
    }
}
