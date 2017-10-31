/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.response

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AttributeHeaderTest extends Specification {

    private static final String ATTRIBUTE_HEADER_JSON = 'executeafm/response/attributeHeader.json'
    private static final String ATTRIBUTE_HEADER_FULL_JSON = 'executeafm/response/attributeHeaderFull.json'

    def "should serialize full"() {
        expect:
        that new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name', [new TotalHeaderItem('sum')]),
                jsonEquals(resource(ATTRIBUTE_HEADER_FULL_JSON))
    }

    def "should serialize"() {
        expect:
        that new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name'),
                jsonEquals(resource(ATTRIBUTE_HEADER_JSON))
    }

    def "should deserialize"() {
        when:
        AttributeHeader header = readObjectFromResource("/$ATTRIBUTE_HEADER_JSON", AttributeHeader)

        then:
        header.name == 'Name'
        header.localIdentifier == 'a1'
        header.uri == '/gdc/md/project_id/obj/123'
        header.identifier == 'attr.dataset.name'
    }

    def "should deserialize full"() {
        when:
        AttributeHeader header = readObjectFromResource("/$ATTRIBUTE_HEADER_FULL_JSON", AttributeHeader)

        then:
        header.name == 'Name'
        header.localIdentifier == 'a1'
        header.uri == '/gdc/md/project_id/obj/123'
        header.identifier == 'attr.dataset.name'
        header.totalItems.size() == 1
        header.totalItems.first().name == 'sum'
        header.toString()
    }
}
