package com.gooddata.collections;

import java.util.Collections;
import java.util.List;

import static com.gooddata.util.Validate.notNull;

/**
 * Wrapper over pageable GDC list.
 *
 * @param <E> type of collection elements
 */
public class PageableList<E> {

    private final List<E> items;
    private final Paging paging;

    /**
     * Creates empty list with no next page.
     */
    public PageableList() {
        this(Collections.<E>emptyList(), null);
    }

    /**
     * Creates list wrapping provided items and next page.
     *
     * @param items  to be wrapped
     * @param paging page description
     */
    public PageableList(final List<E> items, final Paging paging) {
        this.items = notNull(items, "items");
        this.paging = paging;
    }

    /**
     * Return page items.
     *
     * @return page items
     */
    public List<E> getItems() {
        return items;
    }

    /**
     * Returns description of the next page.
     *
     * @return next page, might be <code>null</code>
     */
    public Page getNextPage() {
        return paging == null ? null : paging.getNext();
    }
}
