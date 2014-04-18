package com.gooddata.md;

import com.gooddata.GoodDataException;

/**
 * Metadata object couldn't be created
 */
public class ObjCreateException extends GoodDataException {
    public <T extends Obj> ObjCreateException(T obj, Throwable cause) {
        super("Can't create metadata object: " + obj.getClass().getSimpleName(), cause);
    }
}
