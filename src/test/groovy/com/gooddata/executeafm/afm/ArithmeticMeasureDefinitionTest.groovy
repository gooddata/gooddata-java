/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import com.gooddata.executeafm.UriObjQualifier
import net.javacrumbs.jsonunit.JsonMatchers
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ArithmeticMeasureDefinitionTest extends Specification {

    private static final String ARITHMETIC_MEASURE_DEFINITION_JSON = 'executeafm/afm/arithmeticMeasureDefinition.json'

    def "should serialize"() {
        expect:
        that new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum"), JsonMatchers.jsonEquals(resource(ARITHMETIC_MEASURE_DEFINITION_JSON))
    }

    def "should deserialize"() {
        when:
        ArithmeticMeasureDefinition measure = readObjectFromResource("/$ARITHMETIC_MEASURE_DEFINITION_JSON", ArithmeticMeasureDefinition)

        then:
        measure.getOperator() == "sum"
        measure.getMeasures() == ["localIdentifier1", "localIdentifier2"]
    }

    def "should throw when getting used object qualifier"() {
        given:
        ArithmeticMeasureDefinition measure = new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum")

        when:
        measure.getObjQualifier()

        then:
        thrown(UnsupportedOperationException)
    }

    def "should return empty list when getting used object qualifiers"() {
        expect:
        new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum").getObjQualifiers().isEmpty()
    }

    def "should throw when calling withObjectUriQualifier"() {
        given:
        ArithmeticMeasureDefinition measure = new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum")

        when:
        measure.withObjUriQualifier(new UriObjQualifier("/gdc/md/a12332"))

        then:
        thrown(UnsupportedOperationException)
    }

    def "should return same object when calling withObjUriQualifiers"() {
        setup:
        ArithmeticMeasureDefinition measure = new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum")

        expect:
        measure.withObjUriQualifiers(Mock(ObjQualifierConverter)) == measure

    }
}
