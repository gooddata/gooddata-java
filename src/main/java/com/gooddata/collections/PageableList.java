package com.gooddata.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import static com.gooddata.util.Validate.notNull;

/**
 * Wrapper over pageable GDC list.
 *
 * @param <E> type of collection elements
 */
public class PageableList<E> implements List<E> {

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
     * Returns description of the next page.
     *
     * @return next page, might be <code>null</code>
     */
    public Page getNextPage() {
        return paging == null ? null : paging.getNext();
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        return items.contains(o);
    }

    @Override
    public Iterator<E> iterator() {
        return items.iterator();
    }

    @Override
    public Object[] toArray() {
        return items.toArray();
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return items.toArray(a);
    }

    @Override
    public boolean add(final E e) {
        return items.add(e);
    }

    @Override
    public boolean remove(final Object o) {
        return items.remove(o);
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return items.containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends E> c) {
        return items.addAll(c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends E> c) {
        return items.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return items.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return items.retainAll(c);
    }

    @Override
    public void clear() {
        items.clear();
    }

    @Override
    public E get(final int index) {
        return items.get(index);
    }

    @Override
    public E set(final int index, final E element) {
        return items.set(index, element);
    }

    @Override
    public void add(final int index, final E element) {
        items.add(index, element);
    }

    @Override
    public E remove(final int index) {
        return items.remove(index);
    }

    @Override
    public int indexOf(final Object o) {
        return items.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return items.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        return items.listIterator();
    }

    @Override
    public ListIterator<E> listIterator(final int index) {
        return items.listIterator(index);
    }

    @Override
    public List<E> subList(final int fromIndex, final int toIndex) {
        return items.subList(fromIndex, toIndex);
    }
}
