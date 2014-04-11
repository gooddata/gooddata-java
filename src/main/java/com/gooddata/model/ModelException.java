package com.gooddata.model;

import com.gooddata.GoodDataException;

/**
 * Represents error in ModelService
 */
public class ModelException extends GoodDataException {
    public ModelException(String message) {
        super(message);
    }

    public ModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
