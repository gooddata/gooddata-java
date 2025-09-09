/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm.filter

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ValueAttributeFilterElementsTest extends Specification {

    private static final String ELEMENTS_JSON = 'executeafm/afm/valueAttributeFilterElements.json'

    def "should serialize"() {
        expect:
        that new ValueAttributeFilterElements(['val1', 'val2']), jsonEquals(resource(ELEMENTS_JSON))
    }

    def "should deserialize"() {
        when:
        ValueAttributeFilterElements elements = readObjectFromResource("/$ELEMENTS_JSON", ValueAttributeFilterElements)

        then:
        elements.values == ['val1', 'val2']
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(ValueAttributeFilterElements).verify()
    }
}

