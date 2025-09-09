/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.executeafm.result

import spock.lang.Specification


class DataTest extends Specification {

    def "should have correct null"() {
        given:
        Data data = Data.NULL

        expect:
        !data.isList()
        !data.isValue()
        data.isNull()
        data.textValue() == null
        data
    }

}

