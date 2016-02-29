/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.JsonMatchers;
import org.testng.annotations.Test;

public class AttributeSortTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testSerializePlain() throws Exception {
        final AttributeSort attributeSort = new AttributeSort("pk");
        assertThat(attributeSort, net.javacrumbs.jsonunit.JsonMatchers.jsonEquals("pk"));
    }

    @Test
    public void testDeserializePlain() throws Exception {
        final AttributeSort attributeSort = MAPPER.readValue("\"pk\"", AttributeSort.class);
        assertThat(attributeSort, is(notNullValue()));
        assertThat(attributeSort.getValue(), is("pk"));
    }

    @Test
    public void testSerializeLink() throws Exception {
        final AttributeSort attributeSort = new AttributeSort("/gdc/md/PROJECT_ID/obj/1806", true);
        assertThat(attributeSort, JsonMatchers.serializesToJson("/md/attributeSort.json"));
    }

    @Test
    public void testDeserializeLink() throws Exception {
        final AttributeSort attributeSort = MAPPER.readValue(getClass().getResourceAsStream("/md/attributeSort.json"), AttributeSort.class);
        assertThat(attributeSort, is(notNullValue()));
        assertThat(attributeSort.getValue(), is("/gdc/md/PROJECT_ID/obj/1806"));
    }
}