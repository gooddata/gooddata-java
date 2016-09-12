/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.warehouse;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;

/**
 * Warehouse instance doesn't exist.
 */
public class WarehouseUserNotFoundException extends GoodDataException {

    private final String userUri;

    public WarehouseUserNotFoundException(String uri, GoodDataRestException cause) {
        super("Warehouse user " + uri + " was not found", cause);
        this.userUri = uri;
    }

    public String getUserUri() {
        return userUri;
    }
}
