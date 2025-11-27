/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md

import spock.lang.Specification

class IdentifiersAndUrisTest extends Specification {
    private static final String IDENTIFIER_1 = "IDENTIFIER_1";
    private static final String IDENTIFIER_2 = "IDENTIFIER_2";
    private static final String URI_1 = "URI_1";
    private static final String URI_2 = "URI_2";
    private static final IDENTIFIERS = [
            new IdentifierAndUri(IDENTIFIER_1, URI_1),
            new IdentifierAndUri(IDENTIFIER_2, URI_2)
    ]

    def "should return Identifier to URI map"() {
        IdentifiersAndUris identifiersAndUris = new IdentifiersAndUris(IDENTIFIERS)

        expect:
        identifiersAndUris.asIdentifierToUri() == [(IDENTIFIER_1): URI_1, (IDENTIFIER_2): URI_2]
    }

    def "should return URI to Identifier map"() {
        IdentifiersAndUris identifiersAndUris = new IdentifiersAndUris(IDENTIFIERS)

        expect:
        identifiersAndUris.asUriToIdentifier() == [(URI_1): IDENTIFIER_1, (URI_2): IDENTIFIER_2]
    }
}
