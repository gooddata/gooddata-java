/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc

import org.apache.hc.client5.http.classic.HttpClient
import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.message.BasicClassicHttpResponse
import org.apache.hc.core5.http.protocol.HttpContext
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.message.BasicHttpRequest
import spock.lang.Specification

class ShadowHttpClient4AdapterTest extends Specification {

    private final HttpClient client = Mock()
    private final DataStoreService.ShadowHttpClient4Adapter adapter = new DataStoreService.ShadowHttpClient4Adapter(client)

    def "should convert absolute HttpUriRequest to Classic request"() {
        given:
        def response = new BasicClassicHttpResponse(200, "OK")
        client.execute(_ as ClassicHttpRequest, _ as HttpContext) >> response

        when:
        CloseableHttpResponse result = adapter.execute(new HttpGet("https://example.com/foo"))

        then:
        result.statusLine.statusCode == 200
        result.getProtocolVersion().protocol == "HTTP"
    }

    def "should merge host and relative path"() {
        given:
        def response = new BasicClassicHttpResponse(204, "No Content")
        client.execute(_ as HttpHost, _ as ClassicHttpRequest,
                _ as HttpContext) >> response

        when:
        def httpHost = new org.apache.http.HttpHost("example.com", 443, "https")
        def request = new BasicHttpRequest("DELETE", "/webdav/file.txt")
        def result = adapter.execute(httpHost, request)

        then:
        result.statusLine.statusCode == 204
        0 * client.execute(_ as ClassicHttpRequest, _)
    }
}
