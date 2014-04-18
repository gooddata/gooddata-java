package com.gooddata.md;

import com.gooddata.GoodDataException;

/**
 * Metadata object of the given URI doesn't exist
 */
public class ObjNotFoundException extends GoodDataException {

    private final String uri;

    public <T extends Obj> ObjNotFoundException(String uri, Class<T> cls, Exception e) {
        super(cls.getSimpleName() + " not found " + uri, e);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
