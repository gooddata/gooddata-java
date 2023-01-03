/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service


import com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider
import spock.lang.Specification

import static net.jadler.Jadler.*

abstract class GoodDataITBase<T> extends Specification {

    protected GoodData gd
    protected GoodDataEndpoint endpoint

    void setup() {
        initJadler().withDefaultResponseContentType('application/json')
        endpoint = new GoodDataEndpoint('localhost', port(), 'http')
        gd = new GoodData(
                new LoginPasswordGoodDataRestProvider(
                    endpoint,
                    createGoodDataSettings(),
                    'sdk@gooddata.com', 'sdk'
                ),
        )
    }

    protected GoodDataSettings createGoodDataSettings() {
        final GoodDataSettings settings = new GoodDataSettings()
        settings.setPollSleep(0)
        return settings
    }

    protected abstract T getService()

    void cleanup() {
        closeJadler()
    }
}
