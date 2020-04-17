/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;

import java.util.Optional;

/**
 * The interface of the function that converts {@link IdentifierObjQualifier} to the matching {@link UriObjQualifier}.
 */
@FunctionalInterface
public interface ObjQualifierConverter {

    /**
     * Convert provided {@link IdentifierObjQualifier} to the matching {@link UriObjQualifier}.
     *
     * @param identifierObjQualifier
     *         The identifier that must be converted.
     *
     * @return The optional matching {@link UriObjQualifier} obtained by the conversion.
     */
    Optional<UriObjQualifier> convertToUriQualifier(IdentifierObjQualifier identifierObjQualifier);
}
