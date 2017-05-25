/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import org.testng.annotations.Test;

import java.util.List;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DatasetTest {

    @SuppressWarnings("deprecation")
    @Test
    public void shouldDeserialize() throws Exception {
        final Dataset dataset = readObjectFromResource("/md/dataset.json", Dataset.class);

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
        assertThat(dataset.getDataUploadsUri(), is("/gdc/md/PROJECT_ID/data/uploads/688536"));
        assertThat(dataset.hasUploadConfiguration(), is(false));
        assertThat(dataset.getUploadConfigurationLink(), is(nullValue()));
        assertThat(dataset.getUploadConfigurationUri(), is(nullValue()));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final Dataset dataset = new Dataset("Dataset");
        assertThat(dataset, serializesToJson("/md/dataset-input.json"));
    }

    @Test
    public void testToStringFormat() {
        final Dataset dataset = new Dataset("Dataset");

        assertThat(dataset.toString(), matchesPattern(Dataset.class.getSimpleName() + "\\[.*\\]"));
    }

}