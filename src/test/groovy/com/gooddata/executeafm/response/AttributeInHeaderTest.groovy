/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response

import nl.jqno.equalsverifier.EqualsVerifier
import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AttributeInHeaderTest extends Specification {

    private static final String ATTRIBUTE_IN_HEADER_JSON = 'executeafm/response/attributeInHeader.json'

    def "should serialize"() {
        expect:
        that new AttributeInHeader('Some attr', '/gdc/md/project_id/obj/567', 'attr.some.id'),
                jsonEquals(resource(ATTRIBUTE_IN_HEADER_JSON))
    }

    def "should deserialize"() {
        when:
        AttributeInHeader attributeInHeader = readObjectFromResource("/$ATTRIBUTE_IN_HEADER_JSON", AttributeInHeader)

        then:
        attributeInHeader.name == 'Some attr'
        attributeInHeader.uri == '/gdc/md/project_id/obj/567'
        attributeInHeader.identifier == 'attr.some.id'
        attributeInHeader.toString()
    }

    def "should verify equals"() {
        expect:
        EqualsVerifier.forClass(AttributeInHeader).usingGetClass().verify()
    }
}
