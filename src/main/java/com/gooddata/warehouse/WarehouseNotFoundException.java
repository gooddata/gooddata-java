/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;

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
