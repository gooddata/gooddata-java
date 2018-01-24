/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AttributeSortTest {

    @Test
    public void testSerializePlain() throws Exception {
        final AttributeSort attributeSort = new AttributeSort("pk");
        assertThat(attributeSort, net.javacrumbs.jsonunit.JsonMatchers.jsonEquals("pk"));
    }

    @Test
    public void testDeserializePlain() throws Exception {
        final AttributeSort attributeSort = OBJECT_MAPPER.readValue("\"pk\"", AttributeSort.class);
        assertThat(attributeSort, is(notNullValue()));
        assertThat(attributeSort.getValue(), is("pk"));
    }

    @Test
    public void testSerializeLink() throws Exception {
        final AttributeSort attributeSort = new AttributeSort("/gdc/md/PROJECT_ID/obj/1806", true);
        assertThat(attributeSort, jsonEquals(resource("md/attributeSort.json")));
    }

    @Test
    public void testDeserializeLink() throws Exception {
        final AttributeSort attributeSort = readObjectFromResource("/md/attributeSort.json", AttributeSort.class);
        assertThat(attributeSort, is(notNullValue()));
        assertThat(attributeSort.getValue(), is("/gdc/md/PROJECT_ID/obj/1806"));
    }

    @Test
    public void testToStringFormat() {
        final AttributeSort attributeSort = new AttributeSort("/gdc/md/PROJECT_ID/obj/1806", true);

        assertThat(attributeSort.toString(), matchesPattern(AttributeSort.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final AttributeSort attributeSort = new AttributeSort("/gdc/md/PROJECT_ID/obj/1806", true);
        final AttributeSort deserialized = SerializationUtils.roundtrip(attributeSort);

        assertThat(deserialized, jsonEquals(attributeSort));
    }

}