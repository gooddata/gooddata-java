/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class MeasureHeaderItemTest extends Specification {

    private static final String MEASURE_HEADER_ITEM_JSON = 'executeafm/response/measureHeaderItem.json'
    private static final String MEASURE_HEADER_ITEM_FULL_JSON = 'executeafm/response/measureHeaderItemFull.json'

    def "should serialize full"() {
        expect:
        that new MeasureHeaderItem('Name', '#,##0.00', 'm1', '/gdc/md/project_id/obj/123', 'metric.name'),
                jsonEquals(resource(MEASURE_HEADER_ITEM_FULL_JSON))
    }

    def "should serialize"() {
        expect:
        that new MeasureHeaderItem('Name', '#,##0.00', 'm1'),
                jsonEquals(resource(MEASURE_HEADER_ITEM_JSON))
    }

    def "should deserialize"() {
        when:
        MeasureHeaderItem item = readObjectFromResource("/$MEASURE_HEADER_ITEM_JSON", MeasureHeaderItem)

        then:
        item.name == 'Name'
        item.format == '#,##0.00'
        item.localIdentifier == 'm1'
    }

    def "should deserialize full"() {
        when:
        MeasureHeaderItem item = readObjectFromResource("/$MEASURE_HEADER_ITEM_FULL_JSON", MeasureHeaderItem)

        then:
        item.name == 'Name'
        item.format == '#,##0.00'
        item.localIdentifier == 'm1'
        item.uri == '/gdc/md/project_id/obj/123'
        item.identifier == 'metric.name'
        item.toString()
    }

    def "should have properties"() {
        when:
        MeasureHeaderItem item = new MeasureHeaderItem("name", "format", "local")
        item.uri = "uri"
        item.identifier = "identifier"

        then:
        item.uri == "uri"
        item.identifier == "identifier"
    }
}
