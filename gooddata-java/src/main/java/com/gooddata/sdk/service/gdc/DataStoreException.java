/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.common.GoodDataException;

/**
 * DataStore operation problem
 */
public class DataStoreException extends GoodDataException {
    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}

