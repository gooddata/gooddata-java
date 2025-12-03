/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents

import org.springframework.http.HttpMethod
import org.springframework.http.client.ClientHttpRequestFactory
import spock.lang.Specification

class RestTemplateUriPrefixingClientHttpRequestFactoryTest extends Specification {

    private final ClientHttpRequestFactory delegate = Mock()
    private final RestTemplateUriPrefixingClientHttpRequestFactory factory =
            new RestTemplateUriPrefixingClientHttpRequestFactory(delegate, new URI("https://example.com"))

    def "should prefix relative uri before delegating"() {
        when:
        factory.createRequest(new URI("/gdc/projects"), HttpMethod.GET)

        then:
        1 * delegate.createRequest(new URI("https://example.com/gdc/projects"), HttpMethod.GET)
    }

    def "should keep absolute uri untouched"() {
        given:
        def absolute = new URI("https://another.host/foo")

        when:
        factory.createRequest(absolute, HttpMethod.POST)

        then:
        1 * delegate.createRequest(absolute, HttpMethod.POST)
    }
}
