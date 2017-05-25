/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Defines logic for generating page URIs.
 */
public interface Page {

    /**
     * Creates {@link URI} for this page request.
     * <p>
     * Use {@link #updateWithPageParams(UriComponentsBuilder)} if you have URI template and only want to update it with
     * page query params.
     *
     * @param uriBuilder URI builder used for generating page URI
     * @return compiled page URI
     */
    URI getPageUri(final UriComponentsBuilder uriBuilder);

    /**
     * Updates provided URI builder query params according to this page configuration.
     * <p>
     * As {@link #getPageUri(UriComponentsBuilder)} returns expanded page URI it is not very useful for cases that
     * require use of URI template with URI variables. This method allows you to use URI templates and benefit
     * from pagination support implemented in {@link Page} implementations. It is especially useful if you need to handle
     * multiple requests of the same URI template in the same way - e.g. monitor request made by {@link RestOperations}
     * methods.
     * <p>
     * Use this in the situation when you have URI template with placeholders and URI variables separately.
     * This method is useful when you have URI template with placeholders and only want to add query parameters based
     * on this page to it.
     * <p>
     * Use {@link #getPageUri(UriComponentsBuilder)} if you want to get concrete page URI and don't have URI template.
     *
     * @param uriBuilder URI builder used for constructing page URI
     * @return provided and updated builder instance
     * @see RestOperations
     */
    UriComponentsBuilder updateWithPageParams(final UriComponentsBuilder uriBuilder);
}
