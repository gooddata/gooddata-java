/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import com.gooddata.sdk.service.GoodDataEndpoint
import com.gooddata.sdk.service.GoodDataSettings
import com.gooddata.sdk.service.gdc.DataStoreService
import org.springframework.web.reactive.function.client.WebClient
import spock.lang.Specification

class SingleEndpointGoodDataRestProviderTest extends Specification {

    def "should use provided client"() {
        given:
        def webClient = Mock(WebClient)

        when:
        def provider = new SingleEndpointGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), webClient) {}

        then:
        provider.webClient == webClient
    }

    def "should get dataStoreService"() {
        given:
        def webClient = Mock(WebClient)

        when:
        def provider = new SingleEndpointGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), webClient) {}
        def dataStoreService = provider.getDataStoreService({ 'stagingUri' })

        then:
        dataStoreService.isPresent()
    }
}
