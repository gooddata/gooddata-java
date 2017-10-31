/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.afm

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class ExpressionFilterTest extends Specification {

    private static final String EXPRESSION_FILTER_JSON = 'executeafm/afm/expressionFilter.json'

    def "should serialize"() {
        expect:
        that new ExpressionFilter('some expression'), jsonEquals(resource(EXPRESSION_FILTER_JSON))
    }

    def "should deserialize"() {
        when:
        ExpressionFilter filter = readObjectFromResource("/$EXPRESSION_FILTER_JSON", ExpressionFilter)

        then:
        filter.value == 'some expression'
        filter.toString()
    }
}
