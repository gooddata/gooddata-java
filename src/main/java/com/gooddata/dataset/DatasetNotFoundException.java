/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
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
