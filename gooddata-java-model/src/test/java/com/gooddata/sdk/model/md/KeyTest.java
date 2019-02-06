/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class KeyTest {

    public static final String DATA = "/gdc/md/PROJECT_ID/obj/PK_ID";
    public static final String TYPE = "col";
    public static final String JSON = "{\"data\":\"/gdc/md/PROJECT_ID/obj/PK_ID\",\"type\":\"col\"}";

    @Test
    public void testDeserialize() throws Exception {
        final Key key = OBJECT_MAPPER.readValue(JSON, Key.class);
        assertThat(key, is(notNullValue()));
        assertThat(key.getData(), is(DATA));
        assertThat(key.getType(), is(TYPE));
    }

    @Test
    public void testSerialize() throws Exception {
        final Key key = new Key(DATA, TYPE);
        final String json = OBJECT_MAPPER.writeValueAsString(key);
        assertThat(json, is(JSON));
    }

    @Test
    public void testToStringFormat() {
        final Key key = new Key(DATA, TYPE);

        assertThat(key.toString(), matchesPattern(Key.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Key key = new Key(DATA, TYPE);
        final Key deserialized = SerializationUtils.roundtrip(key);

        assertThat(deserialized, jsonEquals(key));
    }
}