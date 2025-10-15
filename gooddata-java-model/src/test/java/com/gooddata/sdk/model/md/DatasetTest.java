/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.apache.commons.lang3.SerializationUtils;
import org.testng.annotations.Test;

import java.util.List;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class DatasetTest {

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

        assertThat(dataset.getDataUploadsUri(), is("/gdc/md/PROJECT_ID/data/uploads/688536"));
        assertThat(dataset.hasUploadConfiguration(), is(false));
        assertThat(dataset.getUploadConfigurationUri(), is(nullValue()));
    }

    @Test
    public void shouldSerialize() throws Exception {
        final Dataset dataset = new Dataset("Dataset");
        assertThat(dataset, jsonEquals(resource("md/dataset-input.json")));
    }

    @Test
    public void testToStringFormat() {
        final Dataset dataset = new Dataset("Dataset");

        assertThat(dataset.toString(), matchesPattern(Dataset.class.getSimpleName() + "\\[.*\\]"));
    }

    @Test
    public void testSerializable() throws Exception {
        final Dataset dataset = new Dataset("Dataset");
        final Dataset deserialized = SerializationUtils.roundtrip(dataset);

        assertThat(deserialized, jsonEquals(dataset));
    }

}
