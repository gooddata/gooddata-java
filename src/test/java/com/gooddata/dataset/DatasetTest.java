/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DatasetTest {

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/dataset.json");
        final Dataset dataset = new ObjectMapper().readValue(stream, Dataset.class);
        assertThat(dataset, is(notNullValue()));

        assertThat(dataset.getIdentifier(), is("dataset.person"));
        assertThat(dataset.getTitle(), is("Person"));
        assertThat(dataset.getUri(), is("/gdc/md/PROJECT_ID/ldm/singleloadinterface/dataset.person"));
    }
}
