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

/**
 * Sets default URI prefix for Spring REST client which by default requires absolute URI.
 * UriPrefixingAsyncClientHttpRequestFactory allows you to specify default hostname and port.
 * <p/>
 * It has to implement both AsyncClientHttpRequestFactory, ClientHttpRequestFactory, Spring
 * would not talk to it otherwise.
 */
class UriPrefixingClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final ClientHttpRequestFactory wrapped;
    private final URI defaultUri;

    /**
     * Creates na instance
     *
     * @param wrapped
     * @param uriPrefix
     */
    UriPrefixingClientHttpRequestFactory(ClientHttpRequestFactory wrapped, URI uriPrefix) {
        this.wrapped = wrapped;
        this.defaultUri = uriPrefix;
    }

    UriPrefixingClientHttpRequestFactory(ClientHttpRequestFactory factory, String hostname, int port, String protocol) {
        this(factory, UriComponentsBuilder.newInstance().scheme(protocol).host(hostname).port(port).build().toUri());
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return wrapped.createRequest(mergeUris(uri), httpMethod);
    }

    private URI mergeUris(URI uri) {
        return UriComponentsBuilder.fromUri(defaultUri)
                .path(uri.getRawPath())
                .query(uri.getRawQuery())
                .fragment(uri.getRawFragment())
                .build().toUri();
    }
}