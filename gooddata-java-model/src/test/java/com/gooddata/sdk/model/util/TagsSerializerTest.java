/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.util;

import org.testng.annotations.Test;

import java.util.Collections;
import java.util.LinkedHashSet;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TagsSerializerTest {

    @Test
    public void shouldSerializeOneTag() throws Exception {
        final TagsTestClass tags = new TagsTestClass(singleton("TAG"));
        final String jsonString = OBJECT_MAPPER.writeValueAsString(tags);
        assertThat(jsonString, is("{\"tags\":\"TAG\"}"));
    }

    @Test
    public void shouldSerializeZeroTags() throws Exception {
        final TagsTestClass tags = new TagsTestClass(Collections.emptySet());
        final String jsonString = OBJECT_MAPPER.writeValueAsString(tags);
        assertThat(jsonString, is("{\"tags\":\"\"}"));
    }

    @Test
    public void shouldSerializeTwoTags() throws Exception {
        final TagsTestClass tags = new TagsTestClass(new LinkedHashSet<>(asList("TAG", "TAG2")));
        final String jsonString = OBJECT_MAPPER.writeValueAsString(tags);
        assertThat(jsonString, is("{\"tags\":\"TAG TAG2\"}"));
    }
}