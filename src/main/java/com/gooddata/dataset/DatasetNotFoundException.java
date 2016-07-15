/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import static java.lang.String.format;

import com.gooddata.GoodDataException;

/**
 * Represents error when dataset of the given id was not found
 */
public class DatasetNotFoundException extends GoodDataException {

    private static final String MESSAGE = "Dataset %s was not found";

    public DatasetNotFoundException(String dataset) {
        super(format(MESSAGE, dataset));
    }

    public DatasetNotFoundException(String dataset, Throwable cause) {
        super(format(MESSAGE, dataset), cause);
    }
}
