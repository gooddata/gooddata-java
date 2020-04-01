/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class OverPeriodDateAttributeTest extends Specification {

    private static final String ATTRIBUTE_JSON = "executeafm/afm/overPeriodDateAttribute.json"

    def "should serialize"() {
        expect:
        OverPeriodDateAttribute dateAttribute = new OverPeriodDateAttribute(
                new UriObjQualifier('/gdc/md/projectId/obj/1'), 2)
        that dateAttribute, jsonEquals(resource(ATTRIBUTE_JSON))
    }

    def "should deserialize"() {
        when:
        OverPeriodDateAttribute dateAttribute = readObjectFromResource("/$ATTRIBUTE_JSON", OverPeriodDateAttribute)

        then:
        dateAttribute.attribute.uri == '/gdc/md/projectId/obj/1'
        dateAttribute.periodsAgo == 2
    }

    def "test serializable"() {
        given:
        OverPeriodDateAttribute dateAttribute = readObjectFromResource("/$ATTRIBUTE_JSON", OverPeriodDateAttribute)
        OverPeriodDateAttribute deserialized = SerializationUtils.roundtrip(dateAttribute)

        expect:
        that deserialized, jsonEquals(dateAttribute)
    }

    def "should throw when required construction parameter is null"() {
        when:
        new OverPeriodDateAttribute(attribute, periodsAgo)

        then:
        thrown(IllegalArgumentException)

        where:
        attribute                                      | periodsAgo
        null                                           | 2
        new UriObjQualifier('/gdc/md/projectId/obj/1') | null
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(OverPeriodDateAttribute)
                .usingGetClass()
                .verify()
    }
}
