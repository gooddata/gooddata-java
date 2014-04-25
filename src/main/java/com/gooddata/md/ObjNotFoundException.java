package com.gooddata.md;

import com.gooddata.GoodDataException;

/**
 * Metadata object of the given URI doesn't exist
 */
public class ObjNotFoundException extends GoodDataException {

    public <T extends Obj> ObjNotFoundException(String uri, Class<T> cls, Exception e) {
        super(cls.getSimpleName() + " not found " + uri, e);
    }

    public <T extends Queryable> ObjNotFoundException(Class<T> cls) {
        super(cls.getSimpleName() + " not found");
    }
}
