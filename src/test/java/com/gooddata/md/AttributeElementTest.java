/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

public class AttributeElementTest {

    private static final String URI = "/gdc/md/PROJECT_ID/obj/1333/elements?id=6959";
    private static final String TITLE = "1165";

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attributeElement.json");
        final AttributeElement element = new ObjectMapper().readValue(stream, AttributeElement.class);
        assertThat(element, is(notNullValue()));

        assertThat(element.getUri(), is(URI));
        assertThat(element.getTitle(), is(TITLE));
    }

}