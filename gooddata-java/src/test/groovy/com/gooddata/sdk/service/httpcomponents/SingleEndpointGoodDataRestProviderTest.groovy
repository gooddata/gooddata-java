/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import com.gooddata.sdk.service.GoodDataEndpoint
import com.gooddata.sdk.service.GoodDataSettings
import org.apache.hc.client5.http.classic.HttpClient
import spock.lang.Specification

class SingleEndpointGoodDataRestProviderTest extends Specification {

    def "should use provided client"() {
        given:
        def client = Mock(HttpClient)
        def builder = Stub(GoodDataHttpClientBuilder) {
            buildHttpClient(_, _, _) >> client
        }

        when:
        def provider = new SingleEndpointGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), builder) {
        }

        then:
        provider.httpClient == client
    }

    def "should get dataStoreService"() {
        when:
        def provider = new SingleEndpointGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), Stub(GoodDataHttpClientBuilder)) {
        }
        def dataStoreService = provider.getDataStoreService({ 'stagingUri' })

        then:
        dataStoreService.isPresent()
    }
}
