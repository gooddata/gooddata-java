/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.executeafm.result

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER

class DataListTest extends Specification {

    def "should serialize simple"() {
        expect:
        OBJECT_MAPPER.writeValueAsString(new DataList([new DataValue('a'), new DataValue('b')])) == '["a","b"]'
    }

    def "should serialize nested"() {
        expect:
        OBJECT_MAPPER.writeValueAsString(new DataList([
                new DataList([new DataValue('a'), new DataValue('b')]),
                new DataList([new DataValue('c'), Data.NULL])])) == '[["a","b"],["c",null]]'
    }

    def "should deserialize simple"() {
        when:
        DataList data = OBJECT_MAPPER.readValue('["a","b"]', DataList)

        then:
        data.size() == 2
        data[0] == new DataValue('a')
        data[1] == new DataValue('b')
        data
    }

    def "should deserialize nested"() {
        when:
        DataList data = OBJECT_MAPPER.readValue('[["a","b"],["c",null]]', DataList)

        then:
        data.size() == 2

        data[0] instanceof DataList
        DataList row0 = data[0] as DataList
        row0.size() == 2

        data[1] instanceof DataList
        DataList row1 = data[1] as DataList
        row1.size() == 2

        row0[0] == new DataValue('a')
        row0[1] == new DataValue('b')

        row1[0] == new DataValue('c')
        row1[1] == Data.NULL
    }

    def "should behave as list"() {
        when:
        DataList data = new DataList([['a', 'b'], ['c', null]] as String[][])

        then:
        data.isList()
        !data.isValue()
        data[1][1] == Data.NULL
        data.asList()[0] == new DataList(['a', 'b'] as String[])

        when:
        data.textValue()

        then:
        thrown(UnsupportedOperationException)
    }
}
