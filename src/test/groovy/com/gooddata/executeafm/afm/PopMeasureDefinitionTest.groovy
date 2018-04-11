/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import com.gooddata.executeafm.IdentifierObjQualifier
import com.gooddata.executeafm.UriObjQualifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class PopMeasureDefinitionTest extends Specification {

    private static final String POP_MEASURE_JSON = 'executeafm/afm/popMeasureDefinition.json'
    private static final String POP_MEASURE_WITH_OFFSET_JSON = 'executeafm/afm/popMeasureDefinitionWithOffset.json'


    def "should serialize"() {
        expect:
        that new PopMeasureDefinition('mId', new UriObjQualifier('/gdc/md/projectId/obj/1'), offset),
                jsonEquals(resource(expectedJsonMeasureDefinition))

        where:
        offset | expectedJsonMeasureDefinition
        null   | POP_MEASURE_JSON
        -2     | POP_MEASURE_WITH_OFFSET_JSON
    }

    def "should deserialize"() {
        when:
        PopMeasureDefinition measure = readObjectFromResource("/$jsonMeasureDefinition", PopMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        (measure.popAttribute as UriObjQualifier).uri == '/gdc/md/projectId/obj/1'
        measure.isAdHoc()
        measure.toString()
        measure.offset == expectedOffset

        where:
        jsonMeasureDefinition        | expectedOffset
        POP_MEASURE_JSON             | null
        POP_MEASURE_WITH_OFFSET_JSON | -2
    }

    def "should copy"() {
        when:
        def measure = new PopMeasureDefinition("mId", new IdentifierObjQualifier("id"), inputOffset)
        def copy = measure.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.measureIdentifier == 'mId'
        copy.objQualifier.uri == 'uri'
        copy.offset == expectedOffset

        where:
        inputOffset | expectedOffset
        null        | null
        -2          | -2
    }

    def "test serializable"() {
        when:
        PopMeasureDefinition measureDefinition = readObjectFromResource("/$jsonMeasureDefinition", PopMeasureDefinition)
        PopMeasureDefinition deserialized = SerializationUtils.roundtrip(measureDefinition)

        then:
        that deserialized, jsonEquals(measureDefinition)

        where:
        jsonMeasureDefinition << [POP_MEASURE_JSON, POP_MEASURE_WITH_OFFSET_JSON]
    }

}
