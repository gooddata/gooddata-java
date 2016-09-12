/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.gooddata.GoodDataException;

/**
 * DataStore operation problem
 */
public class DataStoreException extends GoodDataException {
    public DataStoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
