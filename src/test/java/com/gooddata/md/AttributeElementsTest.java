/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;


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