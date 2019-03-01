/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service

import com.gooddata.sdk.service.gdc.GdcService
import com.gooddata.gdc.Header
import org.apache.http.HttpHeaders

import static net.jadler.Jadler.onRequest
import static net.jadler.Jadler.verifyThatRequest
import static org.hamcrest.CoreMatchers.equalTo
import static org.hamcrest.CoreMatchers.hasItem
import static org.hamcrest.CoreMatchers.startsWith

class GoodDataIT extends GoodDataITBase<GdcService> {

    private static final String USER_AGENT = 'customAgent/X.Y'
    private static final String API_VERSION = '2'

    @Override
    protected GoodDataSettings createGoodDataSettings() {
        final GoodDataSettings settings = super.createGoodDataSettings()
        settings.setUserAgent(USER_AGENT)
        return settings
    }

    @Override
    protected GdcService getService() {
        return gd.gdcService
    }

    def "should send custom userAgent header"() {
        given:
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withStatus(200)

        when:
        service.getRootLinks()

        then:
        verifyThatRequest()
                .havingHeader(HttpHeaders.USER_AGENT, hasItem(startsWith("$USER_AGENT GoodData-Java-SDK")))
                .receivedOnce()
    }

    def "should send API version header"() {
        given:
        onRequest()
                .havingMethodEqualTo("GET")
                .respond()
                .withStatus(200)

        when:
        service.getRootLinks()

        then:
        verifyThatRequest()
                .havingHeader(Header.GDC_VERSION, hasItem(equalTo(API_VERSION)))
                .receivedOnce()
    }
}
