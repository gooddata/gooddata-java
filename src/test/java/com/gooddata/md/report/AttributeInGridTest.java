/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md.report;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gooddata.md.Attribute;
import com.gooddata.md.DisplayForm;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

import static com.gooddata.JsonMatchers.serializesToJson;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.isOneOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AttributeInGridTest {

    public static final String URI = "/URI";
    public static final String ALIAS = "ALIAS";

    @Test
    public void testDeserialization() throws Exception {
        final InputStream is = getClass().getResourceAsStream("/md/report/attributeInGrid.json");
        final AttributeInGrid attr = new ObjectMapper().readValue(is, AttributeInGrid.class);

        assertThat(attr, is(notNullValue()));
        assertThat(attr.getUri(), is(URI));
        assertThat(attr.getAlias(), is(ALIAS));

        assertThat(attr.getTotals(), is(notNullValue()));
        assertThat(attr.getTotals(), hasSize(2));
        final Iterator<Collection<String>> i = attr.getTotals().iterator();
        final Collection<String> subTotal1 = i.next();
        assertThat(subTotal1, hasSize(1));
        assertThat(subTotal1.iterator().next(), is("TOTAL1"));
        final Collection<String> subTotal2 = i.next();
        assertThat(subTotal2, hasSize(2));
        assertThat(subTotal2.iterator().next(), isOneOf("TOTAL2", "TOTAL3"));
        assertThat(subTotal2.iterator().next(), isOneOf("TOTAL2", "TOTAL3"));
    }

    @Test
    public void testSerialization() throws Exception {
        final Collection<Collection<String>> totals = asList((Collection<String>) asList("TOTAL1"),
                asList("TOTAL2", "TOTAL3"));
        final AttributeInGrid attr = new AttributeInGrid(URI, totals, ALIAS);
        assertThat(attr, serializesToJson("/md/report/attributeInGrid.json"));
    }

    @Test
    public void testCreateFromAttribute() throws Exception {
        final Attribute attr = mock(Attribute.class);
        final DisplayForm displayForm = mock(DisplayForm.class);
        when(attr.getDefaultDisplayForm()).thenReturn(displayForm);
        when(displayForm.getUri()).thenReturn(URI);
        when(displayForm.getTitle()).thenReturn(ALIAS);

        final AttributeInGrid attrInGrid = new AttributeInGrid(attr);
        assertThat(attrInGrid.getUri(), is(URI));
        assertThat(attrInGrid.getAlias(), is(ALIAS));
    }
}