package com.gooddata.dataset;

import com.gooddata.GoodDataException;

/**
 * Represents error in DatasetService
 */
public class DatasetException extends GoodDataException {
    public DatasetException(String message) {
        super(message);
    }

    public DatasetException(String message, Throwable cause) {
        super(message, cause);
    }
}
