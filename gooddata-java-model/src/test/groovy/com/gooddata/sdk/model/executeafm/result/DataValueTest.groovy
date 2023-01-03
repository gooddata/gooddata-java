/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER

class DataValueTest extends Specification {

    def "should serialize"() {
        expect:
        OBJECT_MAPPER.writeValueAsString(new DataValue('123')) == '"123"'
    }

    def "should deserialize"() {
        expect:
        OBJECT_MAPPER.readValue('"123"', DataValue) == new DataValue('123')
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(DataValue).usingGetClass().verify()
    }

    def "should behave as value"() {
        when:
        DataValue data = new DataValue('123')

        then:
        data.isValue()
        !data.isList()
        data.textValue() == '123'
        data
    }
}
