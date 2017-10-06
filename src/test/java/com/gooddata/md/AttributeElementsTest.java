/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.testng.annotations.Test;

import java.util.List;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class AttributeElementsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final AttributeElements elements = readObjectFromResource("/md/attributeElements.json", AttributeElements.class);
        assertThat(elements, is(notNullValue()));

        final List<AttributeElement> elementsList = elements.getElements();
        assertThat(elementsList, is(notNullValue()));
        assertThat(elementsList, hasSize(3));
    }

    @Test
    public void shouldVerifyEquals() throws Exception {
        EqualsVerifier.forClass(AttributeElements.class)
                .usingGetClass()
                .verify();
    }

}