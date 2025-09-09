/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.fasterxml.jackson.databind.JsonMappingException;
import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class AttributeSortTest {

    @Test
    public void testSerializePlain() {
        final AttributeSort attributeSort = new AttributeSort("pk");
        assertThat(attributeSort, net.javacrumbs.jsonunit.JsonMatchers.jsonEquals("pk"));
    }

    @Test
    public void testDeserializePlain() throws Exception {
        final AttributeSort attributeSort = OBJECT_MAPPER.readValue("\"pk\"", AttributeSort.class);
        assertThat(attributeSort, is(notNullValue()));
        assertThat(attributeSort.getValue(), is("pk"));
    }

    @Test(expectedExceptions = JsonMappingException.class)
    public void testDeserializeInvalid() throws Exception {
        OBJECT_MAPPER.readValue("123", AttributeSort.class);
    }

    @Test
    public void testSerializeLink() {
        final AttributeSort attributeSort = new AttributeSort("/gdc/md/PROJECT_ID/obj/1806", true);
        assertThat(attributeSort, jsonEquals(resource("md/attributeSort.json")));
    }

    @Test
    public void testDeserializeLink() {
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
