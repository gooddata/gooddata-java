/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.warehouse;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;

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
