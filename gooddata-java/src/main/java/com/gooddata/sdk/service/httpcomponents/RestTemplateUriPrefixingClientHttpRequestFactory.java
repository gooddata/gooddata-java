/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.httpcomponents;

import com.gooddata.sdk.common.UriPrefixer;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * {@link ClientHttpRequestFactory} wrapper that prefixes relative URIs with the GoodData API endpoint.
 * <p>
 * RestTemplate requires absolute URIs, while the SDK uses relative paths everywhere. The historical
 * {@code UriPrefixingClientHttpRequestFactory} from rest-common handled this for HttpClient4. As part of
 * the HttpClient5 migration we keep the same behaviour locally while delegating to the HttpClient5-aware
 * request factory provided by Spring.
 */
class RestTemplateUriPrefixingClientHttpRequestFactory extends AbstractClientHttpRequestFactoryWrapper {

    private final UriPrefixer prefixer;

    RestTemplateUriPrefixingClientHttpRequestFactory(final ClientHttpRequestFactory requestFactory,
                                                     final URI endpointUri) {
        super(notNull(requestFactory, "requestFactory"));
        final URI apiEndpoint = notNull(endpointUri, "endpointUri");
        this.prefixer = new UriPrefixer(apiEndpoint);
    }

    @Override
    protected ClientHttpRequest createRequest(final URI uri, final HttpMethod httpMethod,
                                              final ClientHttpRequestFactory requestFactory) throws IOException {
        final URI targetUri = uri.isAbsolute() ? uri : prefixer.mergeUris(uri);
        return requestFactory.createRequest(targetUri, httpMethod);
    }
}
