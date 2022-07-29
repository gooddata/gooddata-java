/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.md.dashboard.filter

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static spock.util.matcher.HamcrestSupport.that

class DashboardAttributeFilterTest extends Specification {

    static final String FILTER_JSON = '/md/dashboard/filter/dashboardAttributeFilter.json'

    def "deserializes from JSON"() {
        when:
        def filter = readObjectFromResource(FILTER_JSON, DashboardAttributeFilter)

        then:
        filter
        filter.displayForm == '/df/1'
        filter.negativeSelection
        filter.attributeElementUris == [ '/elem/1', '/elem/2' ]
    }

    def "creates new and serializes to JSON"() {
        when:
        def filter = new DashboardAttributeFilter('/df/1', true, [ '/elem/1', '/elem/2' ])

        then:
        that filter, jsonEquals(readStringFromResource(FILTER_JSON))
    }
}
