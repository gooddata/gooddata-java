package com.gooddata.md;

import com.gooddata.GoodDataException;

/**
 * Metadata object of the given URI doesn't exist
 */
public class ObjNotFoundException extends GoodDataException {

    /**
     * Construct a new instance of ObjNotFoundException.
     *
     * @param uri the URI of metadata object you're searching for
     * @param cls class of metadata object you're searching for
     * @param e caused by this exception
     * @param <T> the type of results you're searching for
     */
    public <T extends Obj> ObjNotFoundException(String uri, Class<T> cls, Exception e) {
        super(cls.getSimpleName() + " not found " + uri, e);
    }

    /**
     * Construct a new instance of ObjNotFoundException.
     *
     * @param cls class of metadata object you're searching for
     * @param <T> the type of results you're searching for
     */
    public <T> ObjNotFoundException(Class<T> cls) {
        super(cls.getSimpleName() + " not found");
    }
}
