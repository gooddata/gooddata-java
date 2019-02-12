/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.afm


import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class UriAttributeFilterElementsTest extends Specification {

    private static final String ELEMENTS_JSON = 'executeafm/afm/uriAttributeFilterElements.json'

    def "should serialize"() {
        expect:
        that new UriAttributeFilterElements(['val1', 'val2']), jsonEquals(resource(ELEMENTS_JSON))
    }

    def "should deserialize"() {
        when:
        UriAttributeFilterElements elements = readObjectFromResource("/$ELEMENTS_JSON", UriAttributeFilterElements)

        then:
        elements.uris == ['val1', 'val2']
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(UriAttributeFilterElements).verify()
    }
}
