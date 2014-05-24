/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class FactTest {

    @Test
    public void testDeserialization() throws Exception {
        final Fact fact = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/fact.json"), Fact.class);
        assertThat(fact, is(notNullValue()));
        assertThat(fact.getExpressions(), is(notNullValue()));
        assertThat(fact.getExpressions(), hasSize(1));
    }

    @Test
    public void testSerialization() throws Exception {
        final Fact fact = new Fact("Person Shoe Size", "/gdc/md/PROJECT_ID/obj/COL_ID", "col");
        Assert.assertThat(fact, serializesToJson("/md/fact-input.json"));
    }

}