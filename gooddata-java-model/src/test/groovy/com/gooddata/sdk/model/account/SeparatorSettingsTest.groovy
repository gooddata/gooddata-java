/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.account

import spock.lang.Specification

import static com.gooddata.util.ResourceUtils.readObjectFromResource

class SeparatorSettingsTest extends Specification {

    def "deserializes from json"() {
        when:
        def separators = readObjectFromResource('/account/separators.json', SeparatorSettings)

        then:
        separators
        separators.thousand == '::'
        separators.decimal == '||'
        separators.selfLink == '/gdc/account/profile/ID/settings/separators'
    }
}
