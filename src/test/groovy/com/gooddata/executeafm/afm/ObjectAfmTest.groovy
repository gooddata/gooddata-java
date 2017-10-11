/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import com.gooddata.executeafm.IdentifierObjQualifier
import com.gooddata.executeafm.UriObjQualifier
import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ObjectAfmTest extends Specification {

    private static final String OBJECT_AFM_JSON = 'executeafm/afm/objectAfm.json'
    private static final String EMPTY_JSON = 'executeafm/empty.json'

    private static final UriObjQualifier QUALIFIER = new UriObjQualifier('/gdc/md/projectId/obj/1')

    def "should serialize full"() {
        expect:
        that new ObjectAfm(
                [new AttributeItem(QUALIFIER, 'a1')],
                [new ExpressionFilter('some expression')],
                [new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'mId')],
                [new NativeTotalItem('mId', 'a1', 'a2')]
        ),
                jsonEquals(resource(OBJECT_AFM_JSON))
    }

    def "should serialize"() {
        expect:
        that new ObjectAfm(),
                jsonEquals(resource(EMPTY_JSON))
    }

    def "should deserialize"() {
        when:
        ObjectAfm objectAfm = readObjectFromResource("/$EMPTY_JSON", ObjectAfm)

        then:
        objectAfm.measures == null
        objectAfm.filters == null
        objectAfm.attributes == null
        objectAfm.nativeTotals == null
    }

    def "should deserialize full"() {
        when:
        ObjectAfm objectAfm = readObjectFromResource("/$OBJECT_AFM_JSON", ObjectAfm)

        then:
        objectAfm.measures.size() == 1
        objectAfm.filters.size() == 1
        objectAfm.attributes.size() == 1
        objectAfm.nativeTotals.size() == 1
    }

    def "should get attribute and measure"() {
        given:
        def attribute = new AttributeItem(QUALIFIER, 'localIdA')
        def measure = new MeasureItem(new SimpleMeasureDefinition(QUALIFIER), 'localIdM')
        def afm = new ObjectAfm([attribute], null, [measure], null)

        expect:
        afm.getAttribute('localIdA') == attribute
        afm.getMeasure('localIdM') == measure
    }

    @Unroll
    def "should throw when get for nonexistent #item"() {
        when:
        new ObjectAfm()."get$item"()

        then:
        thrown(IllegalArgumentException)

        where:
        item << ['Attribute', 'Measure']
    }

    def "should add properties"() {
        when:
        def afm = new ObjectAfm()
        afm.addFilter(Spy(CompatibilityFilter))
        afm.addAttribute(new AttributeItem(new IdentifierObjQualifier('id'), 'localIdA'))
        afm.addMeasure(new MeasureItem(Spy(MeasureDefinition), 'mId'))
        afm.addNativeTotal(new NativeTotalItem('mId'))

        then:
        !afm.getFilters().isEmpty()
        !afm.getAttributes().isEmpty()
        !afm.getMeasures().isEmpty()
        !afm.getNativeTotals().isEmpty()
    }
}
