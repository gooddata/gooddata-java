/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import org.testng.annotations.Test;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.fail;

public class MultiPageListTest {
    final MultiPageList<Integer> singlePageList = new MultiPageList<>(
            new PageableList<>(singletonList(11), null),
            page -> null
    );

    final MultiPageList<Integer> twoPageList = new MultiPageList<>(
            new PageableList<>(singletonList(11), new Paging("nextpage2")),
            page -> new PageableList<>(singletonList(12), null)
    );

    @Test
    public void totalSize() {
        assertThat(singlePageList.totalSize(), is(1));
        assertThat(twoPageList.totalSize(), is(2));
    }

    @Test
    public void getCurrentPageItems() {
        assertThat(singlePageList.getCurrentPageItems(), is(singletonList(11)));
        assertThat(twoPageList.getCurrentPageItems(), is(singletonList(11)));
    }

    @Test
    public void collectAll() {
        assertThat(singlePageList.collectAll(), is(singletonList(11)));
        assertThat(twoPageList.collectAll(), is(asList(11, 12)));
    }

    @Test
    public void isEmpty() {
        assertThat(new MultiPageList<Integer>(page -> new PageableList<>()), is(empty()));
        assertThat(singlePageList, is(not(empty())));
        assertThat(twoPageList, is(not(empty())));
    }

    @Test
    public void iterator() {
        final Iterator<Integer> iterator = new MultiPageList<>(
                new PageableList<>(singletonList(1), null),
                page -> null
        ).iterator();

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(1));
        assertThat(iterator.hasNext(), is(false));
        try {
            iterator.next();
            fail("expected NoSuchElementException");
        } catch (final NoSuchElementException ignored) {
        }
    }

    @Test
    public void iteratorWithPageProvider() {
        final PageableList<Integer> lastPage = new PageableList<>(singletonList(3), null);
        final PageableList<Integer> list = new MultiPageList<>(
                new PageableList<>(asList(1, 2), new Paging("next")),
                page -> lastPage
        );

        final Iterator<Integer> iterator = list.iterator();
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(1));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(2));
        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.next(), is(3));

        assertThat(iterator.hasNext(), is(false));
        try {
            iterator.next();
            fail("expected NoSuchElementException");
        } catch (final NoSuchElementException ignored) {
        }
    }

    @Test
    public void stream() {
        final PageableList<Integer> list = new MultiPageList<>(
                new PageableList<>(asList(1, 2), null),
                page -> null
        );
        assertThat(list.stream().max(Integer::compareTo).orElse(0), is(2));
    }

    @Test
    public void streamWithPageProvider() {
        final PageableList<Integer> lastPage = new PageableList<>(singletonList(3), null);
        final PageableList<Integer> list = new MultiPageList<>(
                new PageableList<>(asList(1, 2), new Paging("next")),
                page -> lastPage
        );

        assertThat(list.stream().max(Integer::compareTo).orElse(0), is(3));
    }

    @Test(expectedExceptions = IllegalStateException.class)
    public void infiniteLoopShouldBePrevented() {
        final PageableList<Integer> pageableList = new PageableList<>(asList(1, 2), new Paging("next"));
        final PageableList<Integer> list = new MultiPageList<>(
                pageableList,
                page -> pageableList
        );

        list.stream().anyMatch(it -> it == 5);
    }

    @Test
    public void hasNextShouldOnlySwitchPageOnceAndOnlyOnce() {
        final PageableList<Integer> list = new MultiPageList<>(
                new PageableList<>(emptyList(), new Paging("page2")),
                new SinglePageProvider(new PageableList<>(singletonList(2), new Paging("page3")))
        );
        final Iterator<Integer> iterator = list.iterator();

        assertThat(iterator.hasNext(), is(true));
        assertThat(iterator.hasNext(), is(true));

        assertThat(iterator.next(), is(2));

        try {
            iterator.hasNext();
            fail("expected that iterator will try to switch the page again, but it didn't happen");
        } catch (final IllegalStateException ignored) {
        }
    }

    @Test
    public void testDelegateMethods() {
        final PageableList delegate = mock(PageableList.class);
        final MultiPageList<String> list = new MultiPageList<String>(delegate, page -> null);

        list.getNextPage();
        verify(delegate).getNextPage();

        list.hasNextPage();
        verify(delegate).hasNextPage();

        list.getLinks();
        verify(delegate).getLinks();

        list.getPaging();
        verify(delegate).getPaging();

        list.size();
        verify(delegate).size();

        list.contains("x");
        verify(delegate).contains("x");

        list.toArray();
        verify(delegate).toArray();

        list.toArray(new Object[] {"x"});
        verify(delegate).toArray(new Object[] {"x"});

        list.add("a");
        verify(delegate).add("a");

        list.add(1, "a");
        verify(delegate).add(1, "a");

        list.remove(0);
        verify(delegate).remove(0);

        list.remove("b");
        verify(delegate).remove("b");

        list.clear();
        verify(delegate).clear();

        list.containsAll(asList("a", "b"));
        verify(delegate).containsAll(asList("a", "b"));

        list.addAll(asList("a", "b"));
        verify(delegate).addAll(asList("a", "b"));

        list.addAll(1, asList("a", "b"));
        verify(delegate).addAll(1, asList("a", "b"));

        list.removeAll(asList("a", "b"));
        verify(delegate).removeAll(asList("a", "b"));

        list.retainAll(asList("a", "b"));
        verify(delegate).retainAll(asList("a", "b"));

        list.set(0, "x");
        verify(delegate).set(0, "x");

        list.get(0);
        verify(delegate).get(0);

        list.indexOf("x");
        verify(delegate).indexOf("x");

        list.lastIndexOf("x");
        verify(delegate).lastIndexOf("x");

        list.listIterator();
        verify(delegate).listIterator();

        list.listIterator(1);
        verify(delegate).listIterator(1);

        list.subList(0, 1);
        verify(delegate).subList(0, 1);
    }

    /**
     * This provider will only provide one page, but will throw an exception when asked to provide another
     */
    class SinglePageProvider implements Function<Page, PageableList<Integer>> {
        int count = 0;
        final PageableList<Integer> page;

        SinglePageProvider(final PageableList<Integer> page) {
            this.page = page;
        }

        @Override
        public PageableList<Integer> apply(final Page page) {
            count++;
            if(count > 1) {
                throw new IllegalStateException("second page has been requested");
            }

            return this.page;
        }
    }
}
