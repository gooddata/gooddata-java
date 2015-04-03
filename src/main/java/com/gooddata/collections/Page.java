package com.gooddata.collections;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Defines logic for generating page URIs.
 */
public interface Page {

    /**
     * Creates {@link URI} for this page request.
     *
     * @param uriBuilder URI builder used for generating page URI
     * @return compiled page URI
     */
    URI getPageUri(final UriComponentsBuilder uriBuilder);
}
