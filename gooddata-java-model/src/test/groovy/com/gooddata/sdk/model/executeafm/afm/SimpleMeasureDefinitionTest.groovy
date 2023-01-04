/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import com.gooddata.sdk.model.executeafm.afm.filter.NegativeAttributeFilter
import com.gooddata.sdk.model.executeafm.afm.filter.PositiveAttributeFilter
import nl.jqno.equalsverifier.EqualsVerifier
import nl.jqno.equalsverifier.Warning
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class SimpleMeasureDefinitionTest extends Specification {

    private static final String SIMPLE_MEASURE_DEFINITION_JSON = 'executeafm/afm/simpleMeasureDefinition.json'
    private static final String SIMPLE_MEASURE_DEFINITION_FULL_JSON = 'executeafm/afm/simpleMeasureDefinitionFull.json'

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')

    def "should serialize full"() {
        expect:
        that new SimpleMeasureDefinition(QUALIFIER, Aggregation.AVG,
                true, new PositiveAttributeFilter(QUALIFIER, 'foo'), new NegativeAttributeFilter(QUALIFIER, 'foo')),
                jsonEquals(resource(SIMPLE_MEASURE_DEFINITION_FULL_JSON))
    }

    def "should serialize"() {
        expect:
        that new SimpleMeasureDefinition(QUALIFIER),
                jsonEquals(resource(SIMPLE_MEASURE_DEFINITION_JSON))
    }

    def "should deserialize"() {
        when:
        SimpleMeasureDefinition definition = readObjectFromResource("/$SIMPLE_MEASURE_DEFINITION_JSON", SimpleMeasureDefinition)

        then:
        with(definition.item as UriObjQualifier) {
            uri == QUALIFIER.uri
        }
    }

    def "should deserialize full"() {
        when:
        SimpleMeasureDefinition definition = readObjectFromResource("/$SIMPLE_MEASURE_DEFINITION_FULL_JSON", SimpleMeasureDefinition)

        then:
        with(definition?.item as UriObjQualifier) {
            uri == QUALIFIER.uri
        }
        definition?.aggregation == 'avg'
        definition?.computeRatio
        with(definition?.filters?.find { it.class == PositiveAttributeFilter }) {
            it.displayForm?.uri == QUALIFIER.uri
            it.in.elements == ['foo']
        }
        with(definition?.filters?.find { it.class == NegativeAttributeFilter }) {
            it.displayForm?.uri == QUALIFIER.uri
            it.notIn.elements == ['foo']
        }
    }

    def "should have default properties"() {
        when:
        def definition = new SimpleMeasureDefinition(new IdentifierObjQualifier("id"))
        definition.setFilters(null)

        then:
        !definition.hasAggregation()
        !definition.hasComputeRatio()
        !definition.hasFilters()
        !definition.isAdHoc()

        definition.toString()
    }

    def "should set properties"() {
        when:
        def definition = new SimpleMeasureDefinition(new IdentifierObjQualifier("id"))
        definition.setAggregation(Aggregation.AVG)
        definition.setComputeRatio(true)
        definition.addFilter(new PositiveAttributeFilter(new IdentifierObjQualifier("f")))

        then:
        definition.isAdHoc()

        definition.hasAggregation()
        definition.aggregation == 'avg'

        definition.hasComputeRatio()
        definition.getComputeRatio()

        definition.hasFilters()
    }

    def "should copy with uri converter"() {
        when:
        def definition = new SimpleMeasureDefinition(new IdentifierObjQualifier("id"))
        def qualifiersConversionMap = [(new IdentifierObjQualifier("id")): new UriObjQualifier("uri")]
        def copy = definition.withObjUriQualifiers({ identifierQualifier ->
            return Optional.ofNullable(qualifiersConversionMap.get(identifierQualifier))
        })

        then:
        copy.getUri() == 'uri'
        copy.getItem().getUri() == 'uri'
    }

    def "should return the same object when copying with uri converter and uri is already used"() {
        when:
        def definition = new SimpleMeasureDefinition(new UriObjQualifier('uri'))
        def copy = definition.withObjUriQualifiers({ identifierQualifier -> Optional.empty() })

        then:
        definition == copy
    }

    def "should fail when qualifier converter is not provided or cannot convert object's identifier qualifiers"() {
        when:
        def definition = new SimpleMeasureDefinition(new IdentifierObjQualifier("id"))
        definition.withObjUriQualifiers(invalidObjQualifierConverter)

        then:
        thrown(IllegalArgumentException)

        where:
        invalidObjQualifierConverter << [null, { identifierQualifier -> Optional.empty() }]
    }

    def "test serializable"() {
        given:
        SimpleMeasureDefinition measureDefinition = readObjectFromResource("/$SIMPLE_MEASURE_DEFINITION_FULL_JSON", SimpleMeasureDefinition)
        SimpleMeasureDefinition deserialized = SerializationUtils.roundtrip(measureDefinition)

        expect:
        that deserialized, jsonEquals(measureDefinition)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(SimpleMeasureDefinition)
                .suppress(Warning.NONFINAL_FIELDS)
                .usingGetClass()
                .verify()
    }

    def "should return empty collection when qualifiers are requested but none was set"() {
        when:
        def definition = new SimpleMeasureDefinition(null)
        def qualifiers = definition.getObjQualifiers()

        then:
        qualifiers.size() == 0
    }
}
