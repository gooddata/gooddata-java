/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class NestedAttributeTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialization() throws Exception {
        final NestedAttribute attribute = readObjectFromResource("/md/dimensionAttribute.json", NestedAttribute.class);
        assertThat(attribute, is(notNullValue()));

        final Collection<DisplayForm> displayForms = attribute.getDisplayForms();
        assertThat(displayForms, is(notNullValue()));
        assertThat(displayForms, hasSize(1));

        final DisplayForm displayForm = displayForms.iterator().next();
        assertThat(displayForm, is(notNullValue()));

        assertThat(displayForm.getFormOf(), is("/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID"));
        assertThat(displayForm.getExpression(), is("[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]"));
        assertThat(displayForm.getLdmExpression(), isEmptyString());

        final DisplayForm defaultDisplayForm = attribute.getDefaultDisplayForm();
        assertThat(defaultDisplayForm, is(notNullValue()));

        assertThat(defaultDisplayForm.getFormOf(), is("/gdc/md/PROJECT_ID/obj/DF_FORM_OF_ID"));
        assertThat(defaultDisplayForm.getExpression(), is("[/gdc/md/PROJECT_ID/obj/DF_EXPRESSION_ID]"));
        assertThat(defaultDisplayForm.getLdmExpression(), isEmptyString());

        final Collection<Key> primaryKeys = attribute.getPrimaryKeys();
        assertThat(primaryKeys, is(Matchers.notNullValue()));
        assertThat(primaryKeys, hasSize(1));
        assertThat(primaryKeys.iterator().next(), is(Matchers.notNullValue()));

        final Collection<Key> foreignKeys = attribute.getForeignKeys();
        assertThat(foreignKeys, is(Matchers.notNullValue()));
        assertThat(foreignKeys, hasSize(1));
        assertThat(foreignKeys.iterator().next(), is(Matchers.notNullValue()));

        assertThat(attribute.hasDimension(), is(true));
        assertThat(attribute.getDimensionLink(), is("/gdc/md/PROJECT_ID/obj/DIM_ID"));
        assertThat(attribute.getDimensionUri(), is("/gdc/md/PROJECT_ID/obj/DIM_ID"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final NestedAttribute attribute = readObjectFromResource("/md/dimensionAttribute.json", NestedAttribute.class);

        assertThat(attribute.toString(), matchesPattern(NestedAttribute.class.getSimpleName() + "\\[.*\\]"));
    }
}