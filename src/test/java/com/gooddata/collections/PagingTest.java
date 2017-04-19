/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PagingTest {

    @Test
    public void testDeserialization() throws Exception {
        final Paging paging = readObjectFromResource("/collections/paging.json", Paging.class);

        assertThat(paging.getOffset(), is("0"));
        assertThat(paging.getNext(), notNullValue());
        assertThat(paging.getNext().getPageUri(null).toString(), is("next"));
    }

    @Test
    public void testDeserializationNullNext() throws Exception {
        final Paging paging = readObjectFromResource("/collections/paging_no_next.json", Paging.class);

        assertThat(paging.getOffset(), is("0"));
        assertThat(paging.getNext(), nullValue());
    }

    @Test
    public void testDeserializationWithNextOnly() throws Exception {
        final Paging paging = readObjectFromResource("/collections/paging_only_next.json", Paging.class);

        assertThat(paging.getOffset(), is(nullValue()));
        assertThat(paging.getNext(), notNullValue());
        assertThat(paging.getNext().getPageUri(null).toString(), is("/nextUri?offset=17"));
    }

    @Test
    public void testSerialization() throws Exception {
        final Paging paging = new Paging("/nextUri?offset=17");
        assertThat(paging, jsonEquals(resource("collections/paging_only_next.json")));
    }

}