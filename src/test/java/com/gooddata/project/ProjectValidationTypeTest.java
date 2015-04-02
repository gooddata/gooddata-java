package com.gooddata.project;

import org.codehaus.jackson.map.ObjectMapper;
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