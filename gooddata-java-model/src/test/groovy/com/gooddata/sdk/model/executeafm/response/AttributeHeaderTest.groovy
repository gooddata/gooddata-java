/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class AttributeHeaderTest extends Specification {

    private static final String ATTRIBUTE_HEADER_JSON = 'executeafm/response/attributeHeader.json'
    private static final String ATTRIBUTE_HEADER_FULL_JSON = 'executeafm/response/attributeHeaderFull.json'

    private static final AttributeInHeader FORM_OF = new AttributeInHeader('Some attr', '/gdc/md/project_id/obj/567', 'attr.some.id')

    def "should serialize full"() {
        expect:
        that new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name', FORM_OF, [new TotalHeaderItem('sum')]),
                jsonEquals(resource(ATTRIBUTE_HEADER_FULL_JSON))
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

    def "should deserialize full"() {
        when:
        AttributeHeader header = readObjectFromResource("/$ATTRIBUTE_HEADER_FULL_JSON", AttributeHeader)

        then:
        header.name == 'Name'
        header.localIdentifier == 'a1'
        header.uri == '/gdc/md/project_id/obj/123'
        header.identifier == 'attr.dataset.name'
        header.formOf == FORM_OF
        header.totalItems.size() == 1
        header.totalItems.first().name == 'sum'
        header.toString()
    }
}
