/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.md.Obj;

/**
 * Metadata object couldn't be created
 */
public class ObjCreateException extends GoodDataException {

    /**
     * Construct a new instance of ObjCreateException.
     *
     * @param obj   the metadata object you're trying to create
     * @param cause the cause of error
     * @param <T>   the type of metadata object you're trying to create
     */
    public <T extends Obj> ObjCreateException(T obj, Throwable cause) {
        super("Can't create metadata object: " + obj.getClass().getSimpleName(), cause);
    }

    /**
     * Construct a new instance of ObjCreateException.
     *
     * @param message the detail message
     * @param obj     the metadata object you're trying to create
     * @param <T>     the type of metadata object you're trying to create
     */
    public <T extends Obj> ObjCreateException(String message, T obj) {
        super("Can't create metadata object: " + obj.getClass().getSimpleName() + "; Cause: " + message);
    }
}

