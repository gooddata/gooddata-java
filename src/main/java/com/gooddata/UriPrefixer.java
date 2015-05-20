/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;
import static org.springframework.util.StringUtils.trimLeadingCharacter;
import static org.springframework.util.StringUtils.trimTrailingCharacter;

/**
 * Used internally by GoodData SDK to hold and set URI prefix (hostname and port) of all requests.
 */
public class UriPrefixer {

    private final URI uriPrefix;

    /**
     * Construct URI prefixer using given URI prefix (just hostname and port is used)
     *
     * @param uriPrefix the URI prefix
     */
    public UriPrefixer(URI uriPrefix) {
        this.uriPrefix = notNull(uriPrefix, "uriPrefix");
    }

    /**
     * Construct URI prefixer using given URI prefix (just hostname and port is used)
     *
     * @param uriPrefix the URI prefix string
     */
    public UriPrefixer(String uriPrefix) {
        this(URI.create(uriPrefix));
    }

    /**
     * Get the URI prefix
     *
     * @return the URI prefix
     */
    public URI getUriPrefix() {
        return uriPrefix;
    }

    /**
     * Return merged URI prefix (hostname and port) with the given URI (path, query, and fragment URI parts)
     *
     * @param uri the URI its parts (path, query, and fragment) will be merged with URI prefix
     * @return the merged URI
     */
    public URI mergeUris(URI uri) {
        notNull(uri, "uri");
        final String path = trimTrailingCharacter(trimLeadingCharacter(uri.getRawPath(), '/'), '/');
        return UriComponentsBuilder.fromUri(uriPrefix)
                .pathSegment(path)
                .query(uri.getRawQuery())
                .fragment(uri.getRawFragment())
                .build().toUri();
    }

    /**
     * Return merged URI prefix (hostname and port) with the given URI string (path, query, and fragment URI parts)
     *
     * @param uri the URI string its parts (path, query, and fragment) will be merged with URI prefix
     * @return the merged URI
     */
    public URI mergeUris(String uri) {
        notEmpty(uri, "uri");
        return mergeUris(URI.create(uri));
    }
}