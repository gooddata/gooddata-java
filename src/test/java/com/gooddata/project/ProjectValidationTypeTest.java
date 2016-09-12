/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ProjectValidationTypeTest {

    @Test
    public void testSerialize() throws Exception {
        final String myValidationType = new ObjectMapper().writeValueAsString(new ProjectValidationType("myValidationType"));
        assertThat(myValidationType, is("\"myValidationType\""));
    }

    @Test
    public void testDeserialize() throws Exception {
        final ProjectValidationType myValidationType = new ObjectMapper().readValue("\"myValidationType\"", ProjectValidationType.class);
        assertThat(myValidationType, notNullValue());
        assertThat(myValidationType.getValue(), is("myValidationType"));
    }
}