/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import com.gooddata.http.client.GoodDataHttpClient
import com.gooddata.sdk.service.GoodDataEndpoint
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import spock.lang.Specification

import static org.hamcrest.CoreMatchers.instanceOf
import static org.hamcrest.CoreMatchers.is
import static spock.util.matcher.HamcrestSupport.that

class SstGoodDataRestProviderTest extends Specification {

    private static final String SST = 'sst'

    def "should create http client"() {
        given:
        def builder = Stub(HttpClientBuilder) {
            build() >> Stub(CloseableHttpClient)
        }

        expect:
        that SstGoodDataRestProvider.createGoodDataHttpClient(builder, new GoodDataEndpoint(), SST),
                is(instanceOf(GoodDataHttpClient))
    }
}

