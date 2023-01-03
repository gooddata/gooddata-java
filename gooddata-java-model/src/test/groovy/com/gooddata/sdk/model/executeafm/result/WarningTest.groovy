/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class WarningTest extends Specification {

    private static final String WARNING_JSON = 'executeafm/result/warning.json'

    def "should serialize"() {
        expect:
        that new Warning('gdc123', 'Some msg %s %s %s', ['bum', 1, null]),
                jsonEquals(resource(WARNING_JSON))
    }

    def "should deserialize"() {
        when:
        Warning warning = readObjectFromResource("/$WARNING_JSON", Warning)

        then:
        warning.warningCode == 'gdc123'
        warning.message == 'Some msg %s %s %s'
        warning.parameters == ['bum', 1, null]
        warning.toString()
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(Warning).usingGetClass().verify()
    }
}
