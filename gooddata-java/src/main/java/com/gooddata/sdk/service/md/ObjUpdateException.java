/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.md.Updatable;

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
