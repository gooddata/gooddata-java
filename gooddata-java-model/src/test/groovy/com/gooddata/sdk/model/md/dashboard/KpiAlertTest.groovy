/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class KpiAlertTest extends Specification {

    def "deserializes from JSON"() {
        when:
        def kpiAlert = readObjectFromResource('/md/dashboard/kpiAlert-out.json', KpiAlert)

        then:
        kpiAlert
        kpiAlert.title == 'kpi alert'
        kpiAlert.dashboardUri == '/gdc/md/lkljlgpbqziy3mjjtczf5nbeu32ck1iz/obj/75576'
        kpiAlert.kpiUri == '/gdc/md/lkljlgpbqziy3mjjtczf5nbeu32ck1iz/obj/75809'
        kpiAlert.filterContextUri == '/gdc/md/lkljlgpbqziy3mjjtczf5nbeu32ck1iz/obj/75826'
        kpiAlert.threshold == 100.0d
        kpiAlert.triggerCondition == 'aboveThreshold'
        !kpiAlert.wasTriggered()
    }

    def "changes triggered state"() {
        given:
        def kpiAlert = readObjectFromResource('/md/dashboard/kpiAlert-out.json', KpiAlert)

        when:
        def changedKpiAlert = kpiAlert.withTriggeredState(true)

        then:
        changedKpiAlert.wasTriggered()
    }

    def "builds new and serializes to JSON"() {
        when:
        def kpiAlert = new KpiAlert('kpi alert new', '/kpi/1', '/dashboard/1', 100.0d, 'belowThreshold', '/filter/context/1')

        then:
        that kpiAlert, jsonEquals(readStringFromResource('/md/dashboard/kpiAlert-new.json'))
    }
}
