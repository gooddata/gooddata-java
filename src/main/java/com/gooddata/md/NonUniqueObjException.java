package com.gooddata.md;

import com.gooddata.GoodDataException;

import java.util.Collection;

/**
 * More than a single obj instance was found
 */
public class NonUniqueObjException extends GoodDataException {

    public <T extends Queryable> NonUniqueObjException(Class<T> cls, Collection<String> results) {
        super("Expected a single instance of " + cls.getSimpleName().toLowerCase() + " but found " + results);
    }
}
