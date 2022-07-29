/*
 * (C) 2022 GoodData Corporation.
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

class PopMeasureDefinitionTest extends Specification {

    private static final String POP_MEASURE_DEFINITION_JSON = 'executeafm/afm/popMeasureDefinition.json'

    def "should serialize"() {
        expect:
        that new PopMeasureDefinition('mId', new UriObjQualifier('/gdc/md/projectId/obj/1')),
                jsonEquals(resource(POP_MEASURE_DEFINITION_JSON))
    }

    def "should deserialize"() {
        when:
        PopMeasureDefinition measure = readObjectFromResource("/$POP_MEASURE_DEFINITION_JSON", PopMeasureDefinition)

        then:
        measure.measureIdentifier == 'mId'
        measure.popAttribute.uri == '/gdc/md/projectId/obj/1'
        measure.objQualifiers.first().uri == '/gdc/md/projectId/obj/1'
        measure.isAdHoc()
        measure.toString()
    }

    def "should copy with uri converter"() {
        when:
        def measure = new PopMeasureDefinition("mid", new IdentifierObjQualifier("id"))
        def qualifiersConversionMap = [(new IdentifierObjQualifier("id")): new UriObjQualifier("uri")]
        def copy = measure.withObjUriQualifiers({ identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        })

        then:
        copy.popAttribute.uri == 'uri'
    }

    def "should return the same object when copying with uri converter and uri is already used"() {
        when:
        def definition = new PopMeasureDefinition("mid", new UriObjQualifier("uri"))
        def copy = definition.withObjUriQualifiers({ identifierQualifier -> Optional.empty() })

        then:
        definition == copy
    }

    def "should fail when qualifier converter is not provided or cannot convert object's identifier qualifiers"() {
        when:
        def measure = new PopMeasureDefinition("mid", new IdentifierObjQualifier("id"))
        measure.withObjUriQualifiers(invalidObjQualifierConverter)

        then:
        thrown(IllegalArgumentException)

        where:
        invalidObjQualifierConverter << [null, { identifierQualifier -> Optional.empty() }]
    }

    def "test serializable"() {
        given:
        PopMeasureDefinition measureDefinition = readObjectFromResource("/$POP_MEASURE_DEFINITION_JSON", PopMeasureDefinition)
        PopMeasureDefinition deserialized = SerializationUtils.roundtrip(measureDefinition)

        expect:
        that deserialized, jsonEquals(measureDefinition)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(PopMeasureDefinition)
                .usingGetClass()
                .verify()
    }

    def "should return empty collection when qualifiers are requested but none was set"() {
        when:
        def definition = new PopMeasureDefinition("mid", null)
        def qualifiers = definition.getObjQualifiers()

        then:
        qualifiers.size() == 0
    }
}
