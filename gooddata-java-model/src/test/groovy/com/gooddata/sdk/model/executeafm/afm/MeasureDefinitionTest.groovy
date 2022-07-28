/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm

import spock.lang.Specification
import spock.lang.Unroll

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

class MeasureDefinitionTest extends Specification {

    @Unroll
    def "should deserialize as #typeClass"() {
        when:
        MeasureDefinition instance = readObjectFromResource("/executeafm/afm/${type}.json", MeasureDefinition)

        then:
        typeClass.isInstance(instance)

        where:
        type                              | typeClass
        'popMeasureDefinition'            | PopMeasureDefinition
        'simpleMeasureDefinition'         | SimpleMeasureDefinition
        'overPeriodMeasureDefinition'     | OverPeriodMeasureDefinition
        'previousPeriodMeasureDefinition' | PreviousPeriodMeasureDefinition
    }

    def "getUri() should throw exception"() {
        given:
        def definition = Spy(MeasureDefinition)

        when:
        definition.getUri()

        then:
        def exception = thrown(UnsupportedOperationException)
        exception.message == "This definition has no URI"
    }
}
