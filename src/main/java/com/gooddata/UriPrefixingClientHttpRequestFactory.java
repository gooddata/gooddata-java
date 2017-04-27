/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.io.IOException;
import java.net.URI;

import static com.gooddata.util.Validate.notNull;

/**
 * Factory for ClientHttpRequest objects.
 * Sets default URI prefix for Spring REST client which by default requires absolute URI.
 * UriPrefixingAsyncClientHttpRequestFactory allows you to specify default hostname and port.
 */
class UriPrefixingClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final ClientHttpRequestFactory wrapped;
    private final UriPrefixer prefixer;

    /**
     * Create an instance that will wrap the given {@link org.springframework.http.client.ClientHttpRequestFactory}
     * and use the given URI for setting hostname and port of all HTTP requests
     *
     * @param factory   the factory to be wrapped
     * @param uriPrefix the URI for setting hostname and port of all HTTP requests
     */
    UriPrefixingClientHttpRequestFactory(ClientHttpRequestFactory factory, URI uriPrefix) {
        this(factory, new UriPrefixer(uriPrefix));
    }

    /**
     * Create an instance that will wrap the given {@link org.springframework.http.client.ClientHttpRequestFactory}
     * and use the given hostname, port, and protocol for all HTTP requests
     *
     * @param factory  the factory to be wrapped
     * @param endpoint GoodData Platform's endpoint
     */
    UriPrefixingClientHttpRequestFactory(ClientHttpRequestFactory factory, GoodDataEndpoint endpoint) {
        this(factory, new UriPrefixer(notNull(endpoint, "prefixer").toUri()));
    }

    private UriPrefixingClientHttpRequestFactory(final ClientHttpRequestFactory factory, final UriPrefixer prefixer) {
        this.wrapped = notNull(factory, "factory");
        this.prefixer =  notNull(prefixer, "prefixer");
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        final URI merged = prefixer.mergeUris(uri);
        return wrapped.createRequest(merged, httpMethod);
    }

}