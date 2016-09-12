/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.GoodDataException;

/**
 * Metadata object couldn't be updated
 */
public class ObjUpdateException extends GoodDataException {

    /**
     * Construct a new instance of ObjUpdateException.
     *
     * @param obj   the metadata object you're trying to update
     * @param cause the cause of error
     * @param <T>   the type of metadata object you're trying to update
     */
    public <T extends Updatable> ObjUpdateException(T obj, Throwable cause) {
        super("Can't update metadata object: " + obj.getClass().getSimpleName(), cause);
    }

    /**
     * Construct a new instance of ObjUpdateException.
     *
     * @param message the detail message
     * @param obj     the metadata object you're trying to update
     * @param <T>     the type of metadata object you're trying to update
     */
    public <T extends Updatable> ObjUpdateException(String message, T obj) {
        super("Can't update metadata object: " + obj.getClass().getSimpleName() + "; Cause: " + message);
    }
}
