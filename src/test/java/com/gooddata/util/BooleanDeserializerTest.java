/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BooleanDeserializerTest {

    @Test
    public void shouldDeserializeIntegerTrue() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new BooleanIntegerClass(true));

        final JsonNode node = OBJECT_MAPPER.readTree(json);
        assertThat(node.path("foo").numberValue().intValue(), is(1));

        final BooleanIntegerClass moo = OBJECT_MAPPER.readValue(json, BooleanIntegerClass.class);
        assertThat(moo.isFoo(), is(true));
    }

    @Test
    public void shouldDeserializeIntegerFalse() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new BooleanIntegerClass(false));

        final JsonNode node = OBJECT_MAPPER.readTree(json);
        assertThat(node.path("foo").numberValue().intValue(), is(0));

        final BooleanIntegerClass moo = OBJECT_MAPPER.readValue(json, BooleanIntegerClass.class);
        assertThat(moo.isFoo(), is(false));
    }

    @Test
    public void shouldDeserializeStringTrue() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new BooleanStringClass(true));

        final JsonNode node = OBJECT_MAPPER.readTree(json);
        assertThat(node.path("foo").textValue(), is("1"));

        final BooleanStringClass moo = OBJECT_MAPPER.readValue(json, BooleanStringClass.class);
        assertThat(moo.isFoo(), is(true));
    }

    @Test
    public void shouldDeserializeStringFalse() throws Exception {
        final String json = OBJECT_MAPPER.writeValueAsString(new BooleanStringClass(false));

        final JsonNode node = OBJECT_MAPPER.readTree(json);
        assertThat(node.path("foo").textValue(), is("0"));

        final BooleanStringClass moo = OBJECT_MAPPER.readValue(json, BooleanStringClass.class);
        assertThat(moo.isFoo(), is(false));
    }

}