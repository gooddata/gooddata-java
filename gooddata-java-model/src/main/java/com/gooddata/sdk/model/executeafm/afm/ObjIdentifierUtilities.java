/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm;

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier;
import com.gooddata.sdk.model.executeafm.ObjQualifier;
import com.gooddata.sdk.model.executeafm.UriObjQualifier;

import java.util.function.Function;

import static com.gooddata.util.Validate.notNull;
import static java.lang.String.format;

/**
 * The utilities for conversion of the objects that use {@link IdentifierObjQualifier} to objects that use {@link UriObjQualifier}.
 */
abstract class ObjIdentifierUtilities {

    /**
     * Copy {@code objectToBeCopied} via provided {@code objectCopyFactory} in case when {@code qualifierForPossibleConversion} is of {@link
     * IdentifierObjQualifier} type. Otherwise the original object is returned.
     *
     * @param objectToBeCopied
     *         The object that should be copied in case {@code qualifierForPossibleConversion} is of {@link IdentifierObjQualifier} type. The parameter must
     *         not be null.
     * @param qualifierForPossibleConversion
     *         The qualifier that defines if {@code objectToBeCopied} will be copied or not. In case when it is of the {@link IdentifierObjQualifier} type,
     *         it will be converted via {@code qualifierConverter} and used in copy of the {@code objectToBeCopied}. The parameter must not be null.
     * @param objectCopyFactory
     *         The factory method that accepts the result of the {@code qualifierForPossibleConversion} conversion in form of {@link UriObjQualifier} and
     *         returns new copy of the {@code objectToBeCopied}. The parameter must not be null.
     * @param qualifierConverter
     *         The convert that can convert {@code qualifierForPossibleConversion} into its matching {@link UriObjQualifier} form. The parameter must not be
     *         null.
     * @param <R>
     *         The type of the object that should be copied.
     *
     * @return Copy of the {@code objectToBeCopied} in case when {@code qualifierForPossibleConversion} was of {@link IdentifierObjQualifier} type. Otherwise
     * the original object is returned.
     *
     * @throws IllegalArgumentException
     *         The exception is thrown when required parameter is null.
     */
    static <R> R copyIfNecessary(final R objectToBeCopied,
                                 final ObjQualifier qualifierForPossibleConversion,
                                 final Function<UriObjQualifier, R> objectCopyFactory,
                                 final ObjQualifierConverter qualifierConverter) {
        notNull(objectToBeCopied, "objectToBeCopied");
        notNull(qualifierForPossibleConversion, "qualifierForPossibleConversion");
        notNull(objectCopyFactory, "objectCopyFactory");
        notNull(qualifierConverter, "qualifierConverter");

        if (qualifierForPossibleConversion instanceof IdentifierObjQualifier) {
            final IdentifierObjQualifier identifierQualifierToConvert = (IdentifierObjQualifier) qualifierForPossibleConversion;
            return copyWithUriQualifier(identifierQualifierToConvert, objectCopyFactory, qualifierConverter);
        }
        return objectToBeCopied;
    }

    private static <R> R copyWithUriQualifier(final IdentifierObjQualifier identifierQualifierToConvert,
                                              final Function<UriObjQualifier, R> objectCopyFactory,
                                              final ObjQualifierConverter qualifierConverter) {
        return qualifierConverter.convertToUriQualifier(identifierQualifierToConvert)
                .map(objectCopyFactory)
                .orElseThrow(() -> buildExceptionForFailedConversion(identifierQualifierToConvert));
    }

    private static IllegalArgumentException buildExceptionForFailedConversion(final IdentifierObjQualifier qualifierFailedToConvert) {
        return new IllegalArgumentException(format("Supplied converter does not provide conversion for '%s'!", qualifierFailedToConvert));
    }

}
