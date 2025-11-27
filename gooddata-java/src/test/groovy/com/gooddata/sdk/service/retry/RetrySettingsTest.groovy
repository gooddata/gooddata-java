/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.retry

import spock.lang.Specification
import spock.lang.Unroll

class RetrySettingsTest extends Specification {

    private RetrySettings retrySettings

    def setup() {
        retrySettings = new RetrySettings()
    }

    @Unroll
    def "value #value is #correct for method"() {

        expect:
        try {
            retrySettings."set${variable}"(value)
            assert retrySettings."get${variable}"() == value
        } catch (IllegalArgumentException e) {
            assert correct == false
        }
        where:
        value | correct | variable
        1     | true    | "RetryCount"
        0     | false   | "RetryCount"
        -1    | false   | "RetryCount"
        10    | true    | "RetryCount"
        0     | false   | "RetryInitialInterval"
        -1    | false   | "RetryInitialInterval"
        1     | true    | "RetryInitialInterval"
        10    | true    | "RetryInitialInterval"
        0     | false   | "RetryMaxInterval"
        -1    | false   | "RetryMaxInterval"
        1     | true    | "RetryMaxInterval"
        10    | true    | "RetryMaxInterval"
        1     | false   | "RetryMultiplier"
        0     | false   | "RetryMultiplier"
        1.1   | true    | "RetryMultiplier"
        0.9   | false   | "RetryMultiplier"
        10    | true    | "RetryMultiplier"
    }

}
