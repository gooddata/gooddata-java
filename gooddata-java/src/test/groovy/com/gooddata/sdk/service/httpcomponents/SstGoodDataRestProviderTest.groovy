/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import com.gooddata.http.client.GoodDataHttpClient
import com.gooddata.sdk.service.GoodDataEndpoint
import com.gooddata.sdk.service.GoodDataSettings
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import spock.lang.Specification

import static com.gooddata.sdk.service.httpcomponents.SstGoodDataRestProvider.createHttpClient
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
        that createHttpClient(builder, new GoodDataEndpoint(), SST), is(instanceOf(GoodDataHttpClient))
    }

    def "should provide GoodDataHttpClient"() {
        when:
        def provider = new SstGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), SST)

        then:
        provider.restTemplate
        provider.httpClient instanceof GoodDataHttpClient
    }
}
