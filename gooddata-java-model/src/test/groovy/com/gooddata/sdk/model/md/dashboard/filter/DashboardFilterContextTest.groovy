/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class DashboardFilterContextTest extends Specification {

    def "deserializes from JSON"() {
        when:
        def context = readObjectFromResource('/md/dashboard/filter/filterContext-out.json', DashboardFilterContext)

        then:
        context
        context.title == 'filterContext'
        context.filters*.class == [ DashboardDateFilter, DashboardAttributeFilter ]
    }

    def "creates new and serializes to JSON"() {
        given:
        def dateFilter = DashboardDateFilter.relativeDateFilter(-1, -1, 'GDC.time.year', null)
        def attributeFilter = new DashboardAttributeFilter('/df/1', false, [ '/elem/1' ])

        when:
        def context = new DashboardFilterContext([dateFilter, attributeFilter])

        then:
        that context, jsonEquals(readStringFromResource('/md/dashboard/filter/filterContext-new.json'))
    }
}
