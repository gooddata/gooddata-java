/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.IdentifierObjQualifier
import com.gooddata.sdk.model.executeafm.UriObjQualifier
import com.gooddata.sdk.model.executeafm.afm.filter.CompatibilityFilter
import com.gooddata.sdk.model.executeafm.afm.filter.ExpressionFilter
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AfmTest extends Specification {

    private static final String OBJECT_AFM_JSON = 'executeafm/afm/afm.json'
    private static final String EMPTY_JSON = 'executeafm/empty.json'

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')

    def "should serialize full"() {
        expect:
        that new Afm(
                [new AttributeItem(QUALIFIER, 'a1')],
                [new ExpressionFilter('some expression')],
                [new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'mId')],
                [new NativeTotalItem('mId', ['a1', 'a2'])]
        ),
                jsonEquals(resource(OBJECT_AFM_JSON))
    }

    def "should serialize"() {
        expect:
        that new Afm(),
                jsonEquals(resource(EMPTY_JSON))
    }

    def "should deserialize"() {
        when:
        Afm afm = readObjectFromResource("/$EMPTY_JSON", Afm)

        then:
        afm.measures == null
        afm.filters == null
        afm.attributes == null
        afm.nativeTotals == null
    }

    def "should deserialize full"() {
        when:
        Afm afm = readObjectFromResource("/$OBJECT_AFM_JSON", Afm)

        then:
        afm.measures.size() == 1
        afm.filters.size() == 1
        afm.attributes.size() == 1
        afm.nativeTotals.size() == 1
    }

    def "should get attribute and measure"() {
        given:
        def attribute = new AttributeItem(QUALIFIER, 'localIdA')
        def measure = new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'localIdM')
        def afm = new Afm([attribute], null, [measure], null)

        expect:
        afm.getAttribute('localIdA') == attribute
        afm.getMeasure('localIdM') == measure
    }

    @Unroll
    def "should throw when get for nonexistent #item"() {
        when:
        new Afm()."get$item"()

        then:
        thrown(IllegalArgumentException)

        where:
        item << ['Attribute', 'Measure']
    }

    def "should add properties"() {
        when:
        def afm = new Afm()
        afm.addFilter(Spy(CompatibilityFilter))
        afm.addAttribute(new AttributeItem(new IdentifierObjQualifier('id'), 'localIdA'))
        afm.addMeasure(new MeasureItem(Spy(MeasureDefinition), 'mId'))
        afm.addNativeTotal(new NativeTotalItem('mId', []))

        then:
        !afm.getFilters().isEmpty()
        !afm.getAttributes().isEmpty()
        !afm.getMeasures().isEmpty()
        !afm.getNativeTotals().isEmpty()
    }
}
