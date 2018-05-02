/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import com.gooddata.executeafm.IdentifierObjQualifier
import com.gooddata.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class OverPeriodMeasureDefinitionTest extends Specification {

    private static final String path = 'executeafm/afm'
    private static final String SINGLE_ATTRIBUTE_JSON = "$path/overPeriodMeasureDefinition.json"
    private static final String TWO_ATTRIBUTES_JSON = "$path/overPeriodMeasureDefinitionWithTwoAttributes.json"

    def "should serialize"() {
        expect:
        OverPeriodMeasureDefinition measure = new OverPeriodMeasureDefinition(
                'mId', [new OverPeriodDateAttribute(new UriObjQualifier('/gdc/md/projectId/obj/1'), 2)]
        )
        that measure, jsonEquals(resource(SINGLE_ATTRIBUTE_JSON))
    }

    def "should deserialize when single attribute is provided"() {
        when:
        OverPeriodMeasureDefinition measure = readObjectFromResource("/$SINGLE_ATTRIBUTE_JSON", OverPeriodMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        measure.getObjQualifiers()[0].uri == '/gdc/md/projectId/obj/1'
        measure.dateAttributes.size() == 1
        measure.dateAttributes[0].periodsAgo == 2
        measure.dateAttributes[0].attribute.uri == '/gdc/md/projectId/obj/1'
        measure.isAdHoc()
        measure.toString()
    }

    def "should deserialize when more attributes are provided"() {
        when:
        OverPeriodMeasureDefinition measure = readObjectFromResource("/$TWO_ATTRIBUTES_JSON", OverPeriodMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        measure.getObjQualifiers()[0].uri == '/gdc/md/projectId/obj/1'
        measure.getObjQualifiers()[1].uri == '/gdc/md/projectId/obj/2'
        measure.dateAttributes.size() == 2
        measure.dateAttributes[0].periodsAgo == 2
        measure.dateAttributes[0].attribute.uri == '/gdc/md/projectId/obj/1'
        measure.dateAttributes[1].periodsAgo == 1
        measure.dateAttributes[1].attribute.uri == '/gdc/md/projectId/obj/2'
        measure.isAdHoc()
        measure.toString()
    }

    def "test serializable"() {
        given:
        OverPeriodMeasureDefinition measureDefinition = readObjectFromResource("/$SINGLE_ATTRIBUTE_JSON", OverPeriodMeasureDefinition)
        OverPeriodMeasureDefinition deserialized = SerializationUtils.roundtrip(measureDefinition)

        expect:
        that deserialized, jsonEquals(measureDefinition)
    }

    def "should throw when required construction parameter is null or empty"() {
        when:
        new OverPeriodMeasureDefinition(measureIdentifier, dateAttributes)

        then:
        thrown(IllegalArgumentException)

        where:
        measureIdentifier | dateAttributes
        'mId'             | null
        'mId'             | []
        null              | [new OverPeriodDateAttribute(new UriObjQualifier('/gdc/md/projectId/obj/1'), 2)]
    }

    def "should throw when getting used object qualifiers via deprecated method"() {
        when:
        OverPeriodMeasureDefinition measure = new OverPeriodMeasureDefinition('mId', [
                new OverPeriodDateAttribute(new IdentifierObjQualifier('id1'), -2),
        ])
        measure.getObjQualifier()
        then:
        thrown(UnsupportedOperationException)
    }

    def "should throw when try to copy with uri via deprecated method"() {
        when:
        OverPeriodMeasureDefinition measure = new OverPeriodMeasureDefinition('mId', [
                new OverPeriodDateAttribute(new IdentifierObjQualifier('id1'), -2),
        ])
        measure.withObjUriQualifier(new UriObjQualifier("/gdc/md/projectId/obj/1"))
        then:
        thrown(UnsupportedOperationException)
    }

    def "should copy with uri converter"() {
        when:
        OverPeriodMeasureDefinition measure = new OverPeriodMeasureDefinition('mId', [
                new OverPeriodDateAttribute(new IdentifierObjQualifier('id1'), -2),
                new OverPeriodDateAttribute(new IdentifierObjQualifier('id2'), -3),
                new OverPeriodDateAttribute(new UriObjQualifier('/gdc/md/projectId/obj/3'), -4)
        ])
        def qualifiersConversionMap = [
                (new IdentifierObjQualifier("id2")): new UriObjQualifier("/gdc/md/projectId/obj/2"),
                (new IdentifierObjQualifier("id1")): new UriObjQualifier("/gdc/md/projectId/obj/1")
        ]
        def copy = measure.withObjUriQualifiers({ identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        })

        then:
        measure.measureIdentifier == 'mId'
        copy.dateAttributes[0].attribute.uri == '/gdc/md/projectId/obj/1'
        copy.dateAttributes[0].periodsAgo == -2
        copy.dateAttributes[1].attribute.uri == '/gdc/md/projectId/obj/2'
        copy.dateAttributes[1].periodsAgo == -3
        copy.dateAttributes[2].attribute.uri == '/gdc/md/projectId/obj/3'
        copy.dateAttributes[2].periodsAgo == -4
    }

    def "should fail when qualifier converter is not provided or cannot convert object's identifier qualifiers"() {
        when:
        OverPeriodMeasureDefinition measure = new OverPeriodMeasureDefinition('mId', [
                new OverPeriodDateAttribute(new IdentifierObjQualifier('id1'), -2)
        ])
        measure.withObjUriQualifiers(invalidObjQualifierConverter)

        then:
        thrown(IllegalArgumentException)

        where:
        invalidObjQualifierConverter << [null, { identifierQualifier -> Optional.empty() }]
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(OverPeriodMeasureDefinition)
                .usingGetClass()
                .verify()
    }
}
