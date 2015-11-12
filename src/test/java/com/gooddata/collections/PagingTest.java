package com.gooddata.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PagingTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/collections/paging.json");
        final Paging paging = new ObjectMapper().readValue(stream, Paging.class);

        assertThat(paging.getOffset(), is("0"));
        assertThat(paging.getNext(), notNullValue());
        assertThat(paging.getNext().getPageUri(null).toString(), is("next"));
    }

    @Test
    public void testDeserializationNullNext() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/collections/paging_no_next.json");
        final Paging paging = new ObjectMapper().readValue(stream, Paging.class);

        assertThat(paging.getOffset(), is("0"));
        assertThat(paging.getNext(), nullValue());
    }

    @Test
    public void testDeserializationWithNextOnly() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/collections/paging_only_next.json");
        final Paging paging = new ObjectMapper().readValue(stream, Paging.class);

        assertThat(paging.getOffset(), is(nullValue()));
        assertThat(paging.getNext(), notNullValue());
        assertThat(paging.getNext().getPageUri(null).toString(), is("/nextUri?offset=17"));
    }

    @Test
    public void testSerialization() throws Exception {
        final Paging paging = new Paging("/nextUri?offset=17");
        assertThat(paging, serializesToJson("/collections/paging_only_next.json"));
    }

}