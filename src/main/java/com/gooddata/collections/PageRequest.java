package com.gooddata.collections;

import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static com.gooddata.util.Validate.notNull;

/**
 * User defined page with desired offset and limit.
 */
public class PageRequest implements Page {

    private final int offset;
    private final int limit;

    /**
     * Creates new page with provided values.
     *
     * @param offset page offset
     * @param limit  item count on a page
     */
    public PageRequest(final int offset, final int limit) {
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
