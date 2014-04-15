/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.GoodDataException;

/**
 * Represents error in DatasetService
 */
public class DatasetNotFoundException extends GoodDataException {

    public DatasetNotFoundException(String dataset, Throwable cause) {
        super("Dataset " + dataset + "was not found", cause);
    }
}
