/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter

import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDate

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static com.gooddata.sdk.model.md.dashboard.filter.DashboardDateFilter.*
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class DashboardDateFilterTest extends Specification {

    static final String GRANULARITY = 'GDC.time.year'
    static final String DATE_DATASET_URI = '/ds/uri'

    def "deserializes from JSON"() {
        when:
        def filter = readObjectFromResource(filterJson(RELATIVE_FILTER_TYPE), DashboardDateFilter)

        then:
        filter
        filter.from == '-6'
        filter.to == '-1'
        filter.granularity == GRANULARITY
        filter.type == 'relative'
        filter.dataSet == DATE_DATASET_URI
    }

    @Unroll
    def "creates new #type filter and serializes to JSON"() {
        expect:
        that filter, jsonEquals(readStringFromResource(filterJson(type)))

        where:
        type                 | filter
        RELATIVE_FILTER_TYPE | relativeDateFilter(-6, -1, GRANULARITY, DATE_DATASET_URI)
        ABSOLUTE_FILTER_TYPE | absoluteDateFilter(LocalDate.of(2019, 06, 01), LocalDate.of(2019, 06, 30), DATE_DATASET_URI)
    }

    static filterJson(final String filterType) {
        "/md/dashboard/filter/dashboardDateFilter-${filterType}.json"
    }
}
