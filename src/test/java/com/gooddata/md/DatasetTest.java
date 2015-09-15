/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.List;

public class DatasetTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/md/dataset.json");
        final Dataset dataset = new ObjectMapper().readValue(stream, Dataset.class);

        assertThat(dataset, is(notNullValue()));

        final List<String> ties = dataset.getTies();
        assertThat(ties, is(notNullValue()));
        assertThat(ties, hasSize(1));

        assertThat(dataset.getMode(), is(""));

        final List<String> facts = dataset.getFacts();
        assertThat(facts, is(notNullValue()));
        assertThat(facts, hasSize(2));

        final List<String> dataLoadingColumns = dataset.getDataLoadingColumns();
        assertThat(dataLoadingColumns, is(notNullValue()));
        assertThat(dataLoadingColumns, hasSize(0));

        final List<String> attributes = dataset.getAttributes();
        assertThat(attributes, is(notNullValue()));
        assertThat(attributes, hasSize(3));

        assertThat(dataset.getDataUploadsLink(), is("/gdc/md/PROJECT_ID/data/uploads/688536"));
        assertThat(dataset.hasUploadConfiguration(), is(false));
        assertThat(dataset.getUploadConfigurationLink(), is(nullValue()));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final Dataset dataset = new Dataset("Dataset");
        assertThat(dataset, serializesToJson("/md/dataset-input.json"));
    }

}