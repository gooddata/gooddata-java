/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataset;

import com.gooddata.sdk.common.GoodDataException;

import java.util.Collection;

import static java.util.Arrays.asList;

/**
 * Represents error in DatasetService
 */
public class DatasetException extends GoodDataException {

    private final Collection<String> datasets;

    public DatasetException(String message, String dataset) {
        this(message, dataset, null);
    }

    public DatasetException(String message, String dataset, Throwable cause) {
        this(message, asList(dataset), cause);
    }

    public DatasetException(String message, Collection<String> datasets, Throwable cause) {
        super("Load datasets " + datasets + " failed: " + message, cause);
        this.datasets = datasets;
    }

    public DatasetException(String message, Collection<String> datasets) {
        this(message, datasets, null);
    }

    /**
     * Get datasets.
     * @return dataset names
     */
    public Collection<String> getDatasets() {
        return datasets;
    }

}
