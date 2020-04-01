/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.lcm

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource
import static spock.util.matcher.HamcrestSupport.that

class LcmEntitiesTest extends Specification {

    private static final String EMPTY_JSON = 'lcm/lcmEntitiesEmpty.json'
    private static final String LCM_ENTITIES_JSON = 'lcm/lcmEntities.json'

    def "should serialize empty"() {
        expect:
        that new LcmEntities(), jsonEquals(resource(EMPTY_JSON))
    }

    def "should serialize"() {
        expect:
        that new LcmEntities(new LcmEntity('PROJECT_ID', 'PROJECT_TITLE', [project: '/gdc/projects/PROJECT_ID'])),
                jsonEquals(resource(LCM_ENTITIES_JSON))
    }

    def "should deserialize empty"() {
        when:
        LcmEntities lcmEntities = readObjectFromResource("/$EMPTY_JSON", LcmEntities)

        then:
        lcmEntities.size() == 0
        lcmEntities.paging
    }

    def "should deserialize"() {
        when:
        LcmEntities lcmEntities = readObjectFromResource("/$LCM_ENTITIES_JSON", LcmEntities)

        then:
        lcmEntities.size() == 1
        lcmEntities.first().projectId == 'PROJECT_ID'
        lcmEntities.paging
    }
}
