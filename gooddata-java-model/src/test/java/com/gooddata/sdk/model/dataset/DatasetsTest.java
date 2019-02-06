/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.gooddata.sdk.model.gdc.AboutLinks.Link;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DatasetsTest {

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize() throws Exception {
        final Datasets datasets = readObjectFromResource("/dataset/datasetLinks.json", Datasets.class);
        assertThat(datasets, is(notNullValue()));
        assertThat(datasets.getCategory(), is("singleloadinterface"));
        assertThat(datasets.getInstance(), is("MD::LDM::SingleLoadInterface"));
        assertThat(datasets.getSummary(), is("single loading interfaces"));

        final Collection<Link> links = datasets.getLinks();
        assertThat(links, is(notNullValue()));
        assertThat(links, hasSize(1));

        final Link link = links.iterator().next();
        assertThat(link, is(notNullValue()));
        assertThat(link.getIdentifier(), is("dataset.person"));
        assertThat(link.getUri(), is("/gdc/md/PROJECT_ID/ldm/singleloadinterface/dataset.person"));
        assertThat(link.getCategory(), is("dataset-singleloadinterface"));
        assertThat(link.getTitle(), is("Person"));
        assertThat(link.getSummary(), is("dataset single data loading interface specifications"));
    }
}
