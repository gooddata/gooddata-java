/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class PreviousPeriodMeasureDefinitionTest extends Specification {

    private static final String path = 'executeafm/afm'
    private static final String SINGLE_DATA_SET_JSON = "$path/previousPeriodMeasureDefinition.json"
    private static final String TWO_DATA_SETS_JSON = "$path/previousPeriodMeasureDefinitionWithTwoDataSets.json"

    def "should serialize"() {
        expect:
        PreviousPeriodMeasureDefinition measure = new PreviousPeriodMeasureDefinition(
                'mId', [new PreviousPeriodDateDataSet(new UriObjQualifier('/gdc/md/projectId/obj/1'), 2)]
        )
        that measure, jsonEquals(resource(SINGLE_DATA_SET_JSON))
    }

    def "should deserialize when single attribute is provided"() {
        when:
        PreviousPeriodMeasureDefinition measure = readObjectFromResource("/$SINGLE_DATA_SET_JSON", PreviousPeriodMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        measure.getObjQualifiers()[0].uri == '/gdc/md/projectId/obj/1'
        measure.dateDataSets.size() == 1
        measure.dateDataSets[0].periodsAgo == 2
        measure.dateDataSets[0].dataSet.uri == '/gdc/md/projectId/obj/1'
        measure.isAdHoc()
        measure.toString()
    }

    def "should deserialize when more attributes are provided"() {
        when:
        PreviousPeriodMeasureDefinition measure = readObjectFromResource("/$TWO_DATA_SETS_JSON", PreviousPeriodMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        measure.getObjQualifiers()[0].uri == '/gdc/md/projectId/obj/1'
        measure.getObjQualifiers()[1].uri == '/gdc/md/projectId/obj/2'
        measure.dateDataSets.size() == 2
        measure.dateDataSets[0].periodsAgo == 2
        measure.dateDataSets[0].dataSet.uri == '/gdc/md/projectId/obj/1'
        measure.dateDataSets[1].periodsAgo == 1
        measure.dateDataSets[1].dataSet.uri == '/gdc/md/projectId/obj/2'
        measure.isAdHoc()
        measure.toString()
    }

    def "test serializable"() {
        given:
        PreviousPeriodMeasureDefinition measureDefinition = readObjectFromResource("/$SINGLE_DATA_SET_JSON", PreviousPeriodMeasureDefinition)
        PreviousPeriodMeasureDefinition deserialized = SerializationUtils.roundtrip(measureDefinition)

        expect:
        that deserialized, jsonEquals(measureDefinition)
    }

    def "should throw when required construction parameter is null or empty"() {
        when:
        new PreviousPeriodMeasureDefinition(measureIdentifier, dateDataSets)

        then:
        thrown(IllegalArgumentException)

        where:
        measureIdentifier | dateDataSets
        'mId'             | null
        'mId'             | []
        null              | [new PreviousPeriodDateDataSet(new UriObjQualifier('/gdc/md/projectId/obj/1'), 2)]
    }

    def "should copy with uri converter"() {
        when:
        PreviousPeriodMeasureDefinition measure = new PreviousPeriodMeasureDefinition('mId', [
                new PreviousPeriodDateDataSet(new IdentifierObjQualifier('id1'), -2),
                new PreviousPeriodDateDataSet(new IdentifierObjQualifier('id2'), -3),
                new PreviousPeriodDateDataSet(new UriObjQualifier('/gdc/md/projectId/obj/3'), -4)
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
        copy.dateDataSets[0].dataSet.uri == '/gdc/md/projectId/obj/1'
        copy.dateDataSets[0].periodsAgo == -2
        copy.dateDataSets[1].dataSet.uri == '/gdc/md/projectId/obj/2'
        copy.dateDataSets[1].periodsAgo == -3
        copy.dateDataSets[2].dataSet.uri == '/gdc/md/projectId/obj/3'
        copy.dateDataSets[2].periodsAgo == -4
    }

    def "should fail when qualifier converter is not provided or cannot convert object's identifier qualifiers"() {
        when:
        PreviousPeriodMeasureDefinition measure = new PreviousPeriodMeasureDefinition('mId', [
                new PreviousPeriodDateDataSet(new IdentifierObjQualifier('id1'), -2)
        ])
        measure.withObjUriQualifiers(invalidObjQualifierConverter)

        then:
        thrown(IllegalArgumentException)

        where:
        invalidObjQualifierConverter << [null, { identifierQualifier -> Optional.empty() }]
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(PreviousPeriodMeasureDefinition)
                .usingGetClass()
                .verify()
    }
}
