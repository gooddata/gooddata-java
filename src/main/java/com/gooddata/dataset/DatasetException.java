/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.GoodDataException;

/**
 * Represents error in DatasetService
 */
public class DatasetException extends GoodDataException {

    private final String dataset;

    public DatasetException(String message, String dataset) {
        this(message, dataset, null);
    }

    public DatasetException(String message, String dataset, Throwable cause) {
        super("Load dataset " + dataset + " failed: " + message, cause);
        this.dataset = dataset;
    }

    public String getDataset() {
        return dataset;
    }
}
