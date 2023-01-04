/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.model.md.Queryable;

import java.util.Collection;

/**
 * More than a single obj instance was found
 */
public class NonUniqueObjException extends GoodDataException {

    /**
     * Construct a new instance of NonUniqueObjException.
     *
     * @param cls     class of metadata object you're searching for
     * @param results collection of URIs you have found
     * @param <T>     the type of results you're searching for
     */
    public <T extends Queryable> NonUniqueObjException(Class<T> cls, Collection<String> results) {
        super("Expected a single instance of " + cls.getSimpleName().toLowerCase() + " but found " + results);
    }
}
