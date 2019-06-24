/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service

import com.gooddata.sdk.service.authentication.LoginPasswordAuthentication
import spock.lang.Specification

import static net.jadler.Jadler.closeJadler
import static net.jadler.Jadler.initJadler
import static net.jadler.Jadler.port


abstract class GoodDataITBase<T> extends Specification {

    protected GoodData gd
    protected GoodDataEndpoint endpoint

    void setup() {
        initJadler().withDefaultResponseContentType('application/json')
        endpoint = new GoodDataEndpoint('localhost', port(), 'http')
        gd = new GoodData(
                endpoint,
                new LoginPasswordAuthentication('sdk@gooddata.com', 'sdk'),
                createGoodDataSettings()
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
