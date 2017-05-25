/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TagsDeserializerTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void shouldDeserializeOneToken() throws Exception {
        final TagsTestClass tags = MAPPER.readValue("{\"tags\":\"TAG\"}", TagsTestClass.class);
        assertThat(tags.getTags(), hasSize(1));
        assertThat(tags.getTags().iterator().next(), is("TAG"));
    }

    @Test
    public void shouldDeserializeTwoTokens() throws Exception {
        final TagsTestClass tags = MAPPER.readValue("{\"tags\":\"TAG TAG2\"}", TagsTestClass.class);
        assertThat(tags.getTags(), hasSize(2));
        assertThat(tags.getTags(), hasItems("TAG", "TAG2"));
    }

    @Test
    public void shouldDeserializeMultipleWhitespaceSeparatedTokens() throws Exception {
        final TagsTestClass tags = MAPPER.readValue("{\"tags\":\"TAG     TAG2\"}", TagsTestClass.class);
        assertThat(tags.getTags(), hasSize(2));
        assertThat(tags.getTags(), hasItems("TAG", "TAG2"));
    }

}