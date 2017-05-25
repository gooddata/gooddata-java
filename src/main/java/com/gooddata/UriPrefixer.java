/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

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

        final String path = uri.getRawPath();
        final String[] pathSegments = getPathSegments(path);

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uriPrefix)
                .pathSegment(pathSegments)
                .query(uri.getRawQuery())
                .fragment(uri.getRawFragment());
        if (StringUtils.endsWith(path, "/")) {
            builder.path("/");
        }

        return builder
                .build(true) // we have an URI as the input, so it is already encoded
                .toUri();
    }

    private static String[] getPathSegments(final String path) {
        final List<String> pathSegments = UriComponentsBuilder
                .fromPath(path)
                .build(true)
                .getPathSegments();
        return pathSegments.toArray(new String[pathSegments.size()]);
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