/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.executeafm.afm;

import com.gooddata.executeafm.IdentifierObjQualifier;
import com.gooddata.executeafm.UriObjQualifier;

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
