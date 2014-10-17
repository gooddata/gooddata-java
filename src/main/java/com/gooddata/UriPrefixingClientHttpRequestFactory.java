/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static com.gooddata.Validate.notNull;

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
        this.wrapped = notNull(factory, "factory");
        this.prefixer = new UriPrefixer(uriPrefix);
    }

    /**
     * Create an instance that will wrap the given {@link org.springframework.http.client.ClientHttpRequestFactory}
     * and use the given hostname, port, and protocol for all HTTP requests
     *
     * @param factory  the factory to be wrapped
     * @param hostname the hostname for all HTTP requests
     * @param port     the port for all HTTP requests
     * @param protocol the protocol for all HTTP requests
     */
    UriPrefixingClientHttpRequestFactory(ClientHttpRequestFactory factory, String hostname, int port, String protocol) {
        this(factory, UriComponentsBuilder.newInstance().scheme(protocol).host(hostname).port(port).build().toUri());
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return wrapped.createRequest(prefixer.mergeUris(uri), httpMethod);
    }

}