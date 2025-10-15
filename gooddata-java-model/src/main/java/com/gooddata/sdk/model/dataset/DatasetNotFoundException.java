/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import static java.lang.String.format;

import com.gooddata.sdk.common.GoodDataException;

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

