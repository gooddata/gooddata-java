/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class AnalyticalDashboardTest extends Specification {

    def "deserializes from json"() {
        when:
        def dashboard = readObjectFromResource('/md/dashboard/analyticalDashboard-out.json', AnalyticalDashboard)

        then:
        dashboard
        dashboard.title == 'KPIs #1'
        dashboard.filtersContextUri == '/gdc/md/projectID/obj/76102'
        dashboard.widgetUris == ['/gdc/md/projectID/obj/76101']
    }

    def "creates new and serializes to json"() {
        when:
        def dashboard = new AnalyticalDashboard('New KPIs', ['/w/1', '/w/2'], '/fc/1')

        then:
        that dashboard, jsonEquals(readStringFromResource('/md/dashboard/analyticalDashboard-new.json'))
    }
}
