/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;

public class UriPrefixer {

    private final URI defaultUri;

    public UriPrefixer(URI uriPrefix) {
        this.defaultUri = notNull(uriPrefix, "uriPrefix");
    }

    public UriPrefixer(String uriPrefix) {
        this(URI.create(uriPrefix));
    }

    public URI getDefaultUri() {
        return defaultUri;
    }

    public URI mergeUris(URI uri) {
        notNull(uri, "uri");
        return UriComponentsBuilder.fromUri(defaultUri)
                .path(uri.getRawPath())
                .query(uri.getRawQuery())
                .fragment(uri.getRawFragment())
                .build().toUri();
    }

    public URI mergeUris(String uri) {
        notEmpty(uri, "uri");
        return mergeUris(URI.create(uri));
    }
}