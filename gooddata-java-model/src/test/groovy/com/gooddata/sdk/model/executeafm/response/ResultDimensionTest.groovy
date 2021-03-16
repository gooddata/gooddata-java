/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.response

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class ResultDimensionTest extends Specification {

    private static final String RESULT_DIMENSION_JSON = 'executeafm/response/resultDimension.json'

    private static final AttributeInHeader FORM_OF = new AttributeInHeader('Some attr', '/gdc/md/project_id/obj/567', 'attr.some.id')

    def "should serialize"() {
        expect:
        that new ResultDimension(
                new AttributeHeader('Name', 'a1', '/gdc/md/project_id/obj/123', 'attr.dataset.name', FORM_OF),
                new MeasureGroupHeader([new MeasureHeaderItem('Name', '#,##0.00', 'm1')])),
                jsonEquals(resource(RESULT_DIMENSION_JSON))
    }

    def "should deserialize"() {
        when:
        ResultDimension dimension = readObjectFromResource("/$RESULT_DIMENSION_JSON", ResultDimension)

        then:
        dimension.headers.size() == 2
        dimension.headers[0].localIdentifier == 'a1'
        dimension.headers[1].items.first().localIdentifier == 'm1'

        dimension.toString()
    }
}
