/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm

import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class IdentifierObjQualifierTest extends Specification {

    private static final String QUALIFIER_JSON = 'executeafm/identifierObjQualifier.json'

    def "should serialize"() {
        expect:
        that new IdentifierObjQualifier('df.id'), jsonEquals(resource(QUALIFIER_JSON))
    }

    def "should deserialize"() {
        when:
        IdentifierObjQualifier identifierQualifier = readObjectFromResource("/$QUALIFIER_JSON", IdentifierObjQualifier)

        then:
        identifierQualifier.identifier == 'df.id'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(IdentifierObjQualifier).verify()
    }

    def "test serializable"() {
        IdentifierObjQualifier qualifier = readObjectFromResource("/$QUALIFIER_JSON", IdentifierObjQualifier)
        IdentifierObjQualifier deserialized = SerializationUtils.roundtrip(qualifier)

        expect:
        that deserialized, jsonEquals(qualifier)
    }
}
