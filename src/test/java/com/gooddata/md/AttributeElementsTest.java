/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;
import java.util.Set;


public class AttributeElementsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/attributeElements.json");
        final AttributeElements elements = new ObjectMapper().readValue(stream, AttributeElements.class);
        assertThat(elements, is(notNullValue()));

        final List<AttributeElement> elementsList = elements.getElements();
        assertThat(elementsList, is(notNullValue()));
        assertThat(elementsList, hasSize(3));
    }

}