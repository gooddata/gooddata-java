/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import com.gooddata.sdk.service.GoodDataEndpoint
import com.gooddata.sdk.service.GoodDataSettings
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

class LoginPasswordGoodDataRestProviderTest extends Specification {

    def "should use provided WebClient"() {
        given:
        def webClient = Mock(WebClient)

        when:
        def provider = new LoginPasswordGoodDataRestProvider(
                new GoodDataEndpoint(), new GoodDataSettings(), "LOGIN", "PASSWORD", webClient
        )

        then:
        provider.webClient == webClient
    }

    def "should provide dataStoreService"() {
        given:
        def webClient = Mock(WebClient)
        def provider = new LoginPasswordGoodDataRestProvider(
                new GoodDataEndpoint(), new GoodDataSettings(), "LOGIN", "PASSWORD", webClient
        )

        when:
        def dataStoreService = provider.getDataStoreService({ "stagingUri" })

        then:
        dataStoreService.isPresent()
    }
}
