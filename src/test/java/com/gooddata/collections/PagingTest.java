package com.gooddata.collections;

import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PagingTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/collections/paging.json");
        final Paging paging = new ObjectMapper().readValue(stream, Paging.class);

        assertThat(paging.getOffset(), is(0));
        assertThat(paging.getCount(), is(1));
        assertThat(paging.getNext(), notNullValue());
        assertThat(paging.getNext().getPageUri(null).toString(), is("next"));
    }

    @Test
    public void testDeserializationNullNext() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/collections/paging_no_next.json");
        final Paging paging = new ObjectMapper().readValue(stream, Paging.class);

        assertThat(paging.getOffset(), is(0));
        assertThat(paging.getCount(), is(1));
        assertThat(paging.getNext(), nullValue());
    }
}