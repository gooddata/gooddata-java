/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AttributeHeaderTest extends Specification {

    private static final String ATTRIBUTE_HEADER_JSON = 'executeafm/response/attributeHeader.json'
    private static final String ATTRIBUTE_HEADER_EXCEPT_TYPE_JSON = 'executeafm/response/attributeHeaderExceptType.json'
    private static final String ATTRIBUTE_HEADER_FULL_JSON = 'executeafm/response/attributeHeaderFull.json'

    private static final AttributeInHeader FORM_OF = new AttributeInHeader('Some attr', '/gdc/md/project_id/obj/567', 'attr.some.id')

    def "should serialize full"() {
        expect:
        that new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name', 'GDC.time.day_us', FORM_OF, [new TotalHeaderItem('sum')]),
                jsonEquals(resource(ATTRIBUTE_HEADER_FULL_JSON))
    }

    def "should serialize except type"() {
        expect:
        that new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name', FORM_OF, [new TotalHeaderItem('sum')]),
                jsonEquals(resource(ATTRIBUTE_HEADER_EXCEPT_TYPE_JSON))
    }

    def "should serialize"() {
        expect:
        that new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name', FORM_OF),
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
        header.formOf == FORM_OF
    }

    def "should deserialize except type"() {
        when:
        AttributeHeader header = readObjectFromResource("/$ATTRIBUTE_HEADER_EXCEPT_TYPE_JSON", AttributeHeader)

        then:
        header.name == 'Name'
        header.localIdentifier == 'a1'
        header.uri == '/gdc/md/project_id/obj/123'
        header.identifier == 'attr.dataset.name'
        header.type == null
        header.formOf == FORM_OF
        header.totalItems.size() == 1
        header.totalItems.first().name == 'sum'
        header.toString()
    }

    def "should deserialize full"() {
        when:
        AttributeHeader header = readObjectFromResource("/$ATTRIBUTE_HEADER_FULL_JSON", AttributeHeader)

        then:
        header.name == 'Name'
        header.localIdentifier == 'a1'
        header.uri == '/gdc/md/project_id/obj/123'
        header.identifier == 'attr.dataset.name'
        header.type == 'GDC.time.day_us'
        header.formOf == FORM_OF
        header.totalItems.size() == 1
        header.totalItems.first().name == 'sum'
        header.toString()
    }
}
