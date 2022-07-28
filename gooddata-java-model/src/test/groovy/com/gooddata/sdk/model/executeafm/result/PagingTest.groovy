/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that


class PagingTest extends Specification {

    private static final String PAGING_JSON = 'executeafm/result/paging.json'

    def "should serialize"() {
        expect:
        that new Paging([4], [0], [4]),
                jsonEquals(resource(PAGING_JSON))
    }

    def "should deserialize"() {
        when:
        Paging paging = readObjectFromResource("/$PAGING_JSON", Paging)

        then:
        paging.count == [4]
        paging.offset == [0]
        paging.total == [4]
    }

    def "should set properties"() {
        when:
        Paging paging = new Paging()
        paging.offset(1, 2)
        paging.count(3, 4)
        paging.total(5, 6)

        then:
        paging.offset == [1, 2]
        paging.count == [3, 4]
        paging.total == [5, 6]
    }
}
