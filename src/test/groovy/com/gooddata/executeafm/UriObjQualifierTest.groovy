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

class UriObjQualifierTest extends Specification {

    private static final String QUALIFIER_JSON = 'executeafm/uriObjQualifier.json'

    def "should serialize"() {
        expect:
        that new UriObjQualifier('/gdc/md/projectId/obj/1'), jsonEquals(resource(QUALIFIER_JSON))
    }

    def "should deserialize"() {
        when:
        UriObjQualifier uriQualifier = readObjectFromResource("/$QUALIFIER_JSON", UriObjQualifier)

        then:
        uriQualifier.uri == '/gdc/md/projectId/obj/1'
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(UriObjQualifier).verify()
    }

    def "test serializable"() {
        UriObjQualifier uriObjQualifier = readObjectFromResource("/$QUALIFIER_JSON", UriObjQualifier)
        UriObjQualifier deserialized = SerializationUtils.roundtrip(uriObjQualifier)

        expect:
        that deserialized, jsonEquals(uriObjQualifier)
    }
}
