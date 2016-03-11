package com.gooddata.md.data;

import com.gooddata.GoodDataException;

/**
 * Exception thrown when info about dataset with given identifier wasn't found
 */
public class DatasetInfoNotFoundException extends GoodDataException {

    public DatasetInfoNotFoundException(String datasetId) {
        super("Info about dataset " + datasetId + " wasn't found.");
    }
}
