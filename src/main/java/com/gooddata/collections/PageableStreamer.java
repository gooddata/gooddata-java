/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Util class for converting a {@link PageableList} into a {@link Stream}
 */
public class PageableStreamer {

    private PageableStreamer() {
        throw new AssertionError("util class");
    }

    /**
     * Creates a {@link Stream} from the given {@link PageableList}
     *
     * @param pageProvider provides pages from a {@link PageableList}
     * @param <T>          type of items in the stream
     */
    public static <T> Stream<T> stream(final Function<Page, PageableList<T>> pageProvider) {
        final Iterable<PageableList<T>> iterable = () -> new PageIterator<>(pageProvider);
        return StreamSupport.stream(iterable.spliterator(), false).flatMap(Collection::stream);
    }

    /**
     * Creates a {@link Stream} from the given {@link PageableList}
     *
     * @param pageProvider provides pages from a {@link PageableList}
     * @param <T>          type of items in the stream
     */
    static <T> Stream<T> stream(final Function<Page, PageableList<T>> pageProvider, final int pageRequestLimit) {
        final Iterable<PageableList<T>> iterable = () -> new PageIterator<>(pageProvider, pageRequestLimit);
        return StreamSupport.stream(iterable.spliterator(), false).flatMap(Collection::stream);
    }

    private static final class PageIterator<T> implements Iterator<PageableList<T>> {

        private int pageRequestLimit = 100;
        private Page page;
        private final Function<Page, PageableList<T>> pageProvider;

        PageIterator(final Function<Page, PageableList<T>> pageProvider, final int pageRequestLimit) {
            this.pageProvider = pageProvider;
            this.pageRequestLimit = pageRequestLimit;
            this.page = new PageRequest(pageRequestLimit);
        }

        PageIterator(final Function<Page, PageableList<T>> pageProvider) {
            this.pageProvider = pageProvider;
            this.page = new PageRequest(pageRequestLimit);
        }

        @Override
        public boolean hasNext() {
            return page != null;
        }

        @Override
        public PageableList<T> next() {
            final PageableList<T> next = pageProvider.apply(page);
            page = next.getNextPage();
            return next;
        }
    }
}
