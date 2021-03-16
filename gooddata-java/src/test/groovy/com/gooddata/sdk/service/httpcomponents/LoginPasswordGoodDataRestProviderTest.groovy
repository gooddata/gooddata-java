/*
 * (C) 2021 GoodData Corporation.
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

import static com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider.createHttpClient
import static org.hamcrest.CoreMatchers.instanceOf
import static org.hamcrest.CoreMatchers.is
import static spock.util.matcher.HamcrestSupport.that

class LoginPasswordGoodDataRestProviderTest extends Specification {

    private static final String LOGIN = 'LOGIN'
    private static final String PASSWORD = 'PASSWORD'

    def "should create http client"() {
        given:
        def builder = Stub(HttpClientBuilder) {
            build() >> Stub(CloseableHttpClient)
        }

        expect:
        that createHttpClient(builder, new GoodDataEndpoint(), LOGIN, PASSWORD), is(instanceOf(GoodDataHttpClient))
    }

    def "should provide GoodDataHttpClient"() {
        when:
        def provider = new LoginPasswordGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), LOGIN, PASSWORD)

        then:
        provider.restTemplate
        provider.httpClient instanceof GoodDataHttpClient
    }


}
