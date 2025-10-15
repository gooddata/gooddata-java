/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm

import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class LocalIdentifierQualifierTest extends Specification {

    private static final String QUALIFIER_JSON = 'executeafm/localIdentifierQualifier.json'

    def "should serialize"() {
        expect:
        that new LocalIdentifierQualifier('local.identifier'), jsonEquals(resource(QUALIFIER_JSON))
    }

    def "should deserialize"() {
        when:
        LocalIdentifierQualifier qualifier = readObjectFromResource("/$QUALIFIER_JSON", LocalIdentifierQualifier)

        then:
        qualifier.localIdentifier == 'local.identifier'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(LocalIdentifierQualifier).verify()
    }

    def "test serializable"() {
        LocalIdentifierQualifier qualifier = readObjectFromResource("/$QUALIFIER_JSON", LocalIdentifierQualifier)
        LocalIdentifierQualifier deserialized = SerializationUtils.roundtrip(qualifier)

        expect:
        that deserialized, jsonEquals(qualifier)
    }
}

