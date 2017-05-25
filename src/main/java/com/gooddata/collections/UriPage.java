/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;

import static com.gooddata.util.Validate.notNull;

/**
 * {@link Page} implementation wrapping next page link from REST API.
 */
class UriPage implements Page {

    private final UriComponents pageUri;

    /**
     * Creates new instance with defined page URI.
     *
     * @param pageUri page URI
     */
    public UriPage(final String pageUri) {
        try {
            this.pageUri = UriComponentsBuilder.fromUriString(UriUtils.decode(notNull(pageUri, "pageUri"), "UTF-8")).build();
        } catch (final UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * This is effectively no-op. Returns internal URI provided by REST API.
     *
     * @param uriBuilder not used internally, can be null
     * @return next page URI provided by REST API
     */
    @Override
    public URI getPageUri(final UriComponentsBuilder uriBuilder) {
        return pageUri.toUri();
    }

    /**
     * {@inheritDoc}
     * <p>
     * Note that by using this method you might end up with URI that will be different from the one returned by
     * {@link #getPageUri(UriComponentsBuilder)}. Method only copies query parameters and does not care about
     * URI path.
     */
    @Override
    public UriComponentsBuilder updateWithPageParams(final UriComponentsBuilder uriBuilder) {
        notNull(uriBuilder, "uriBuilder");
        for (Entry<String, List<String>> entry : pageUri.getQueryParams().entrySet()) {
            uriBuilder.replaceQueryParam(entry.getKey(), entry.getValue().toArray());
        }
        return uriBuilder;
    }
}
