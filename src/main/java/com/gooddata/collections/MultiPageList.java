/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.Validate.notNull;

/**
 * Adapter over PageableList to offer easy iteration/streaming over multiple pages,
 * lazily loading of pages using specified pageProvider.
 */
public class MultiPageList<T> extends PageableList<T> {
    private final PageableList<T> delegate;
    private final Function<Page, PageableList<T>> pageProvider;

    /**
     * To list all the pages, starting with the very first one.
     *
     * @param pageProvider provider used to load the pages
     */
    public MultiPageList(final Function<Page, PageableList<T>> pageProvider) {
        this(new PageRequest(), pageProvider);
    }

    /**
     * To list all the pages, starting with the startPage specified.
     *
     * @param startPage    page to start with
     * @param pageProvider provider used to load the pages
     */
    public MultiPageList(final Page startPage, final Function<Page, PageableList<T>> pageProvider) {
        this(notNull(pageProvider, "pageProvider can't be null").apply(startPage), pageProvider);
    }

    MultiPageList(final PageableList<T> delegate,
                  final Function<Page, PageableList<T>> pageProvider) {
        this.delegate = notNull(delegate, "delegate can't be null");
        this.pageProvider = notNull(pageProvider, "pageProvider can't be null");
    }

    @Override
    public Iterator<T> iterator() {
        return new PageIterator<>(delegate, pageProvider);
    }

    /**
     * Do not iterate over pages, get the current page items only
     *
     * @return the list of current page items only
     */
    @Override
    public List<T> getCurrentPageItems() {
        return delegate.getCurrentPageItems();
    }

    /**
     * Iterate over multiple pages and collect the results to list
     *
     * @return list with all the results
     */
    @Override
    public List<T> collectAll() {
        return stream().collect(toList());
    }

    /**
     * Returns description of the next page.
     *
     * @return next page, might be <code>null</code>
     */
    @Override
    public Page getNextPage() {
        return delegate.getNextPage();
    }

    /**
     * Signals whether there are more subsequent pages or the last page has been reached
     * @return true if there are more results to come
     */
    @Override
    public boolean hasNextPage() {
        return delegate.hasNextPage();
    }

    /**
     * Returns map of links.
     *
     * @return map of links, might be <code>null</code>
     */
    @Override
    public Map<String, String> getLinks() {
        return delegate.getLinks();
    }

    /**
     * Returns description of the current collection page.
     *
     * @return current collection page, might be <code>null</code>
     */
    @Override
    public Paging getPaging() {
        return delegate.getPaging();
    }

    /**
     * Returns size of the items collection on a current page. If you need the size of entire collection
     * across all pages, use <code>totalSize()</code> instead.
     *
     * @return size of the items collection on a current page
     */
    @Override
    public int size() {
        return delegate.size();
    }

    /**
     * Returns size of entire collection across all pages. It will fetch all the pages, so use wisely.
     * @return size of entire collection across all pages
     */
    public int totalSize() {
        return (int) stream().count();
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    /**
     * Whether current page contains the specified object. If you need to verify whether entire collection
     * across all pages contains the object, use <code>stream().anyMatch(o)</code> instead.
     */
    @Override
    public boolean contains(final Object o) {
        return delegate.contains(o);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return delegate.toArray(a);
    }

    @Override
    public boolean add(final T item) {
        return delegate.add(item);
    }

    @Override
    public boolean remove(final Object o) {
        return delegate.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends T> c) {
        return delegate.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends T> c) {
        return delegate.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return delegate.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return delegate.retainAll(c);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public T get(final int index) {
        return delegate.get(index);
    }

    @Override
    public T set(final int index, final T element) {
        return delegate.set(index, element);
    }

    @Override
    public void add(final int index, final T element) {
        delegate.add(index, element);
    }

    @Override
    public T remove(final int index) {
        return delegate.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<T> listIterator() {
        return delegate.listIterator();
    }

    @Override
    public ListIterator<T> listIterator(final int index) {
        return delegate.listIterator(index);
    }

    @Override
    public List<T> subList(final int fromIndex, final int toIndex) {
        return delegate.subList(fromIndex, toIndex);
    }

    @Override
    public boolean equals(final Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    private static final class PageIterator<T> implements Iterator<T> {

        private PageableList<T> currentPage;
        private Iterator<T> pageItemsIterator;
        private final Function<Page, PageableList<T>> pageProvider;

        PageIterator(final PageableList<T> currentPage, final Function<Page, PageableList<T>> pageProvider) {
            this.pageProvider = pageProvider;
            this.currentPage = currentPage;
            this.pageItemsIterator = currentPage.getItemsIterator();
        }

        @Override
        public boolean hasNext() {
            if (pageItemsIterator.hasNext()) {
                return true;
            }

            if (currentPage.getNextPage() == null) {
                return false;
            }

            final PageableList<T> next = pageProvider.apply(currentPage.getNextPage());
            if (currentPage.getNextPage().equals(next.getNextPage())) {
                throw new IllegalStateException("page provider does not iterate properly, returns the same page");
            }

            if (next.isEmpty() && next.getNextPage() != null) {
                throw new IllegalStateException("page has no results, yet claims there is next page");
            }

            currentPage = next;
            pageItemsIterator = currentPage.getItemsIterator();

            return pageItemsIterator.hasNext();
        }

        @Override
        public T next() {
            if (!pageItemsIterator.hasNext()) {
                throw new NoSuchElementException("no more items left");
            }

            return pageItemsIterator.next();
        }
    }
}
