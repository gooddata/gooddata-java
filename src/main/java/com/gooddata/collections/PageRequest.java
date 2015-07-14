package com.gooddata.collections;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.gooddata.util.Validate.notNull;

/**
 * User defined page with desired offset and limit.
 */
public class PageRequest implements Page {

    private final String offset;
    private final int limit;

    /**
     * Creates new page definition with provided values.
     *
     * @param offset page offset (position in the collection)
     * @param limit  maximal number of returned elements (on a page)
     */
    public PageRequest(final int offset, final int limit) {
        this.offset = String.valueOf(offset);
        this.limit = limit;
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

    @Override
    public URI getPageUri(final UriComponentsBuilder uriBuilder) {
        return notNull(uriBuilder, "uriBuilder")
                .queryParam("offset", offset)
                .queryParam("limit", limit)
                .build().toUri();
    }
}
