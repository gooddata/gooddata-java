/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class DatasetTest {

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize() throws Exception {
        final Dataset dataset = readObjectFromResource("/dataset/dataset.json", Dataset.class);
        assertThat(dataset, is(notNullValue()));

        assertThat(dataset.getIdentifier(), is("dataset.person"));
        assertThat(dataset.getTitle(), is("Person"));
        assertThat(dataset.getUri(), is("/gdc/md/PROJECT_ID/ldm/singleloadinterface/dataset.person"));
    }
}
