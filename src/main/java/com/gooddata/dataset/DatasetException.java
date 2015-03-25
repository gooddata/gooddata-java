/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.GoodDataException;

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

    /**
     * @return string representation of collection containing dataset names
     * @deprecated since this exception may contain more than one dataset, use {@link #getDatasets} instead
     * @see #getDatasets()
     */
    @Deprecated
    public String getDataset() {
        return datasets.toString();
    }
}
