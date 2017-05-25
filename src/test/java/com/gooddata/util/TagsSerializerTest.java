/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TagsSerializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldSerializeOneTag() throws Exception {
        final TagsTestClass tags = new TagsTestClass(singleton("TAG"));
        final String jsonString = MAPPER.writeValueAsString(tags);
        assertThat(jsonString, is("{\"tags\":\"TAG\"}"));
    }

    @Test
    public void shouldSerializeZeroTags() throws Exception {
        final TagsTestClass tags = new TagsTestClass(Collections.emptySet());
        final String jsonString = MAPPER.writeValueAsString(tags);
        assertThat(jsonString, is("{\"tags\":\"\"}"));
    }

    @Test
    public void shouldSerializeTwoTags() throws Exception {
        final TagsTestClass tags = new TagsTestClass(new LinkedHashSet<>(asList("TAG", "TAG2")));
        final String jsonString = MAPPER.writeValueAsString(tags);
        assertThat(jsonString, is("{\"tags\":\"TAG TAG2\"}"));
    }
}