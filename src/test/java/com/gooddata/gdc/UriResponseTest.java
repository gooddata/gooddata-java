/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class UriResponseTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream inputStream = getClass().getResourceAsStream("/gdc/uriResponse.json");
        final UriResponse uriResponse = new ObjectMapper().readValue(inputStream, UriResponse.class);

        assertThat(uriResponse, is(notNullValue()));
        assertThat(uriResponse.getUri(), is("URI"));
    }

    @Test
    public void testSerialization() throws Exception {
        Assert.assertThat(new UriResponse("URI"), serializesToJson("/gdc/uriResponse.json"));
    }
}