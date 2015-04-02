package com.gooddata.collections;

import org.testng.annotations.Test;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;

public class PageableListTest {

    @Test
    public void testCollectionEmpty() {
        final PageableList<Integer> collection = new PageableList<>();
        assertThat(collection.getItems(), notNullValue());
        assertThat(collection.getItems(), empty());
        assertThat(collection.getNextPage(), nullValue());
    }

    @Test
    public void testCollection() {
        final PageableList<Integer> collection = new PageableList<>(Arrays.asList(1, 2, 3), null);
        assertThat(collection.getItems(), notNullValue());
        assertThat(collection.getItems(), hasSize(3));
        assertThat(collection.getNextPage(), nullValue());
    }

    @Test
    public void testCollectionWithPaging() {
        final PageableList<Integer> collection = new PageableList<>(Arrays.asList(1, 2, 3), new Paging(1, 2, "next"));
        assertThat(collection.getItems(), notNullValue());
        assertThat(collection.getItems(), hasSize(3));
        assertThat(collection.getNextPage(), notNullValue());
        assertThat(collection.getNextPage().getPageUri(null).toString(), is("next"));
    }

}