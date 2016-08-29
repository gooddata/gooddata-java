/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.util.Collection;

public class NestedAttributeTest {

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialization() throws Exception {
        final NestedAttribute attribute = new ObjectMapper().readValue(getClass().getResourceAsStream("/md/dimensionAttribute.json"), NestedAttribute.class);
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
}