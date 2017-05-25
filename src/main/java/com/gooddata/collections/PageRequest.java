/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.gooddata.util.Validate.notNull;

/**
 * User defined page with desired offset and limit.
 */
public class PageRequest implements Page {

    public static final Integer DEFAULT_LIMIT = 100;

    private String offset;
    private int limit;

    /**
     * Creates new page definition with default values
     */
    public PageRequest() {
        this(null, DEFAULT_LIMIT);
    }

    /**
     * Creates new page definition with provided values.
     *
     * @param offset page offset (position in the collection)
     * @param limit  maximal number of returned elements (on a page)
     */
    public PageRequest(final int offset, final int limit) {
        this(String.valueOf(offset), limit);
    }

    /**
     * Creates new page definition with provided values.
     *
     * @param offset page offset (position in the collection)
     * @param limit  maximal number of returned elements (on a page)
     */
    public PageRequest(final String offset, final int limit) {
        this.offset = offset;
        this.limit = limit;
    }

    /**
     * Creates new page definition with limit and no offset (usually for the first page)
     * @param limit maximal number of returned elements (on a page)
     */
    public PageRequest(final int limit) {
        this(null, limit);
    }


    public String getOffset() {
        return offset;
    }

    public void setOffset(final String offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    /**
     * Return the limit value or the {@link #DEFAULT_LIMIT} if the limit is lower than or equal to zero
     * @return sanitized limit
     */
    public int getSanitizedLimit() {
        return limit > 0 ? limit : DEFAULT_LIMIT;
    }

    /**
     * Return the {@link #getSanitizedLimit()} if lower than the given maximum or the maximum value
     * @param max maximum value
     * @return sanitized limit
     */
    public int getSanitizedLimit(final int max) {
        final int limit = getSanitizedLimit();
        return limit < max ? limit : max;
    }

    public void setLimit(final int limit) {
        this.limit = limit;
    }

    @Override
    public URI getPageUri(final UriComponentsBuilder uriBuilder) {
        notNull(uriBuilder, "uriBuilder");
        final UriComponentsBuilder copy = UriComponentsBuilder.fromUriString(uriBuilder.build().toUriString());
        return updateWithPageParams(copy).build().toUri();
    }

    @Override
    public UriComponentsBuilder updateWithPageParams(final UriComponentsBuilder uriBuilder) {
        if (offset != null) {
            uriBuilder.replaceQueryParam("offset", offset);
        }
        uriBuilder.replaceQueryParam("limit", limit);
        return uriBuilder;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final PageRequest that = (PageRequest) o;

        if (limit != that.limit) return false;
        return !(offset != null ? !offset.equals(that.offset) : that.offset != null);

    }

    @Override
    public int hashCode() {
        int result = offset != null ? offset.hashCode() : 0;
        result = 31 * result + limit;
        return result;
    }
}
