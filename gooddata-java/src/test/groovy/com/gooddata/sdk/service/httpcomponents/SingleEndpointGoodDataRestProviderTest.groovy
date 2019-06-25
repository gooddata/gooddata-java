/*
 * Copyright (C) 2007-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import com.gooddata.sdk.service.GoodDataEndpoint
import com.gooddata.sdk.service.GoodDataSettings
import org.apache.http.client.HttpClient
import spock.lang.Specification

class SingleEndpointGoodDataRestProviderTest extends Specification {

    def "should use provided client"() {
        given:
        def client = Mock(HttpClient)
        def builder = Stub(GoodDataHttpClientBuilder) {
            buildHttpClient(_, _, _) >> client
        }

        when:
        def provider = new SingleEndpointGoodDataRestProvider(new GoodDataEndpoint(), new GoodDataSettings(), builder) {}

        then:
        provider.httpClient == client
    }
}
