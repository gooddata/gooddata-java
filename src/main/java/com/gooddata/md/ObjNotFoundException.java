/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
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
     * @param e   caused of this exception
     * @param <T> the type of results you're searching for
     */
    public <T extends Obj> ObjNotFoundException(String uri, Class<T> cls, Exception e) {
        super(cls.getSimpleName() + " not found " + uri, e);
    }

    /**
     * Construct a new instance of ObjNotFoundException.
     *
     * @param obj metadata object you're working with
     */
    public ObjNotFoundException(Obj obj) {
        super(obj.getClass().getSimpleName() + " not found " + obj.getUri());
    }

    /**
     * Construct a new instance of ObjNotFoundException.
     *
     * @param uri the URI of metadata object you're searching for
     */
    public ObjNotFoundException(String uri) {
        super("not found " + uri);
    }

    /**
     * Construct a new instance of ObjNotFoundException.
     *
     * @param cls class of metadata object you're searching for
     * @param <T> the type of results you're searching for
     */
    public <T extends Obj> ObjNotFoundException(Class<T> cls) {
        super(cls.getSimpleName() + " not found");
    }
}
