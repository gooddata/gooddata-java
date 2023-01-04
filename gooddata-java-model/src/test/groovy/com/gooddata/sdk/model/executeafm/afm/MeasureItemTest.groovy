/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import nl.jqno.equalsverifier.EqualsVerifier
import org.apache.commons.lang3.SerializationUtils
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class MeasureItemTest extends Specification {

    private static final String MEASURE_ITEM_JSON = 'executeafm/afm/measureItem.json'
    private static final String MEASURE_ITEM_FULL_JSON = 'executeafm/afm/measureItemFull.json'

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')

    def "should serialize full"() {
        expect:
        that new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'lId', 'alias', 'format'),
                jsonEquals(resource(MEASURE_ITEM_FULL_JSON))
    }

    def "should serialize"() {
        expect:
        that new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'lId'),
                jsonEquals(resource(MEASURE_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        MeasureItem measureItem = readObjectFromResource("/$MEASURE_ITEM_JSON", MeasureItem)

        then:
        with(measureItem.definition as SimpleMeasureDefinition) {
            (item as UriObjQualifier).uri == QUALIFIER.uri
        }
        !measureItem.isAdHoc()
    }

    def "should deserialize full"() {
        when:
        MeasureItem measureItem = readObjectFromResource("/$MEASURE_ITEM_FULL_JSON", MeasureItem)

        then:
        with(measureItem.definition as SimpleMeasureDefinition) {
            (item as UriObjQualifier).uri == QUALIFIER.uri
        }
        measureItem.localIdentifier == 'lId'
        measureItem.alias == 'alias'
        measureItem.format == 'format'
        !measureItem.isAdHoc()
    }

    def "should set properties"() {
        when:
        def measureItem = new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'lId')
        measureItem.setAlias('alias')
        measureItem.setFormat('format')

        then:
        measureItem.alias == 'alias'
        measureItem.format == 'format'
        measureItem.toString()
    }

    def "test serializable"() {
        MeasureItem measureItem = readObjectFromResource("/$MEASURE_ITEM_FULL_JSON", MeasureItem)
        MeasureItem deserialized = SerializationUtils.roundtrip(measureItem)

        expect:
        that deserialized, jsonEquals(measureItem)
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(MeasureItem).usingGetClass()
                .withIgnoredFields("alias")
                .withIgnoredFields("format")
                .verify()
    }
}
