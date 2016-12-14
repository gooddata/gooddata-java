/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

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

    @Test
    public void testToStringFormat() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attributeElement.json");
        final AttributeElement element = new ObjectMapper().readValue(stream, AttributeElement.class);

        assertThat(element.toString(), matchesPattern(AttributeElement.class.getSimpleName() + "\\[.*\\]"));
    }
}