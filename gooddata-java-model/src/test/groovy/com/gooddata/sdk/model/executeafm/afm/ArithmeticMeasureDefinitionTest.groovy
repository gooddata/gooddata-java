/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import com.gooddata.sdk.model.executeafm.UriObjQualifier
import net.javacrumbs.jsonunit.JsonMatchers
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
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
        measure.getMeasureIdentifiers() == ["localIdentifier1", "localIdentifier2"]
    }

    def "should return empty list when getting used object qualifiers"() {
        expect:
        new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum").getObjQualifiers().isEmpty()
    }

    def "should return same object when calling withObjUriQualifiers"() {
        setup:
        ArithmeticMeasureDefinition measure = new ArithmeticMeasureDefinition(["localIdentifier1", "localIdentifier2"], "sum")

        expect:
        measure.withObjUriQualifiers(Mock(ObjQualifierConverter)) == measure

    }
}
