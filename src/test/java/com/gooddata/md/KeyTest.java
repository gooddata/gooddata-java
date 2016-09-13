/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class KeyTest {

    public static final ObjectMapper MAPPER = new ObjectMapper();
    public static final String DATA = "/gdc/md/PROJECT_ID/obj/PK_ID";
    public static final String TYPE = "col";
    public static final String JSON = "{\"data\":\"/gdc/md/PROJECT_ID/obj/PK_ID\",\"type\":\"col\"}";

    @Test
    public void testDeserialize() throws Exception {
        final Key key = MAPPER.readValue(JSON, Key.class);
        assertThat(key, is(notNullValue()));
        assertThat(key.getData(), is(DATA));
        assertThat(key.getType(), is(TYPE));
    }

    @Test
    public void testSerialize() throws Exception {
        final Key key = new Key(DATA, TYPE);
        final String json = MAPPER.writeValueAsString(key);
        assertThat(json, is(JSON));
    }
}