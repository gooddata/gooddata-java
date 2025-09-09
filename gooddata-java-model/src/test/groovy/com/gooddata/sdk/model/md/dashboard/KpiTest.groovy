/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard

import com.gooddata.sdk.model.md.dashboard.filter.DateFilterReference
import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class KpiTest extends Specification {

    def "deserializes from JSON"() {
        when:
        def kpi = readObjectFromResource('/md/dashboard/kpi-out.json', Kpi)

        then:
        kpi
        kpi.metricUri == '/gdc/md/lkljlgpbqziy3mjjtczf5nbeu32ck1iz/obj/75808'
        kpi.dateDatasetUri == '/gdc/md/lkljlgpbqziy3mjjtczf5nbeu32ck1iz/obj/75776'
        kpi.comparisonType == 'lastYear'
        kpi.comparisonDirection == 'growIsGood'
        kpi.title == 'Avg. Salary (CSV)'
        kpi.ignoreDashboardFilters*.datasetUri == [ '/date/dataset/1' ]
    }

    def "creates new kpi and serializes to JSON"() {
        when:
        def kpi = new Kpi('KPI 1', '/metric/1', 'lastYear', 'growIsGood', [ new DateFilterReference('/date/dataset/1') ], '/dateDs/1')

        then:
        that kpi, jsonEquals(readStringFromResource('/md/dashboard/kpi-new.json'))
    }
}

