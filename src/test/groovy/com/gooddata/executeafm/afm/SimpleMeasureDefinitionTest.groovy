/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
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

class SimpleMeasureDefinitionTest extends Specification {

    private static final String SIMPLE_MEASURE_DEFINITION_JSON = 'executeafm/afm/simpleMeasureDefinition.json'
    private static final String SIMPLE_MEASURE_DEFINITION_FULL_JSON = 'executeafm/afm/simpleMeasureDefinitionFull.json'

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')

    def "should serialize full"() {
        expect:
        that new SimpleMeasureDefinition(QUALIFIER, Aggregation.AVG,
                true, new PositiveAttributeFilter(QUALIFIER,'foo'), new NegativeAttributeFilter(QUALIFIER, 'foo')),
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
            it.in == ['foo']
        }
        with(definition?.filters?.find { it.class == NegativeAttributeFilter }) {
            it.displayForm?.uri == QUALIFIER.uri
            it.notIn == ['foo']
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

    def "should copy with uri"() {
        when:
        def definition = new SimpleMeasureDefinition(new IdentifierObjQualifier("id"))
        def copy = definition.withObjUriQualifier(new UriObjQualifier("uri"))

        then:
        copy.getUri() == 'uri'
        copy.getObjQualifier().getUri() == 'uri'
    }

    def "test serializable"() {
        SimpleMeasureDefinition measureDefinition = readObjectFromResource("/$SIMPLE_MEASURE_DEFINITION_FULL_JSON", SimpleMeasureDefinition)
        SimpleMeasureDefinition deserialized = SerializationUtils.roundtrip(measureDefinition)

        expect:
        that deserialized, jsonEquals(measureDefinition)
    }
}
