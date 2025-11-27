/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.sdk.model.account

import spock.lang.Specification

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource

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
