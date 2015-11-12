/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class ProjectTemplatesTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final ProjectTemplates templates = new ObjectMapper()
                .readValue(getClass().getResource("/project/project-templates.json"), ProjectTemplates.class);

        assertThat(templates, is(notNullValue()));
        assertThat(templates.getTemplatesInfo(), is(notNullValue()));
        assertThat(templates.getTemplatesInfo(), hasSize(1));
    }
}