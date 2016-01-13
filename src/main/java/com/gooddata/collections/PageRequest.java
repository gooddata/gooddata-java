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

    @Override
    public URI getPageUri(final UriComponentsBuilder uriBuilder) {
        notNull(uriBuilder, "uriBuilder");
        final UriComponentsBuilder copy = UriComponentsBuilder.fromUriString(uriBuilder.build().toUriString());
        return updateWithPageParams(copy).build().toUri();
    }

    @Override
    public UriComponentsBuilder updateWithPageParams(final UriComponentsBuilder uriBuilder) {
        if (offset != null) {
            uriBuilder.queryParam("offset", offset);
        }
        uriBuilder.queryParam("limit", limit);
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
