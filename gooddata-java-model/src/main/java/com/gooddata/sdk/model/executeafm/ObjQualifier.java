/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.sdk.model.md.Obj;

/**
 * Qualifies metadata {@link Obj}
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UriObjQualifier.class, name = "uri"),
        @JsonSubTypes.Type(value = IdentifierObjQualifier.class, name = "identifier")
})
public interface ObjQualifier extends Qualifier {

    /**
     * Returns the qualifier in the form of uri. Default implementation throws {@link UnsupportedOperationException}
     * @return uri qualifier
     */
    default String getUri() {
        throw new UnsupportedOperationException("This qualifier has no URI");
    }
}

