/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

class UriToIdentifierTest extends Specification {

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(UriToIdentifier).usingGetClass().withNonnullFields("uris").verify();
    }

}