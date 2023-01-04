/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.sdk.model.md.Obj;

/**
 * Qualifies metadata {@link Obj} or local AFM object
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ObjQualifier.class),
        @JsonSubTypes.Type(value = LocalIdentifierQualifier.class, name = "localIdentifier")
})
public interface Qualifier {

}
