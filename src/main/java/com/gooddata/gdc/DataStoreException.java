/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
