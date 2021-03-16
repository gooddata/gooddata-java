/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import com.gooddata.sdk.model.gdc.AboutLinks;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class DatasetLinksTest {

    @Test
    public void deserialize() throws Exception {
        final DatasetLinks datasetLinks = readObjectFromResource("/dataset/datasetLinks.json", DatasetLinks.class);
        assertThat(datasetLinks, is(notNullValue()));
        assertThat(datasetLinks.getCategory(), is("singleloadinterface"));
        assertThat(datasetLinks.getInstance(), is("MD::LDM::SingleLoadInterface"));
        assertThat(datasetLinks.getSummary(), is("single loading interfaces"));

        final Collection<AboutLinks.Link> links = datasetLinks.getLinks();
        assertThat(links, is(notNullValue()));
        assertThat(links, hasSize(1));

        final AboutLinks.Link link = links.iterator().next();
        assertThat(link, is(notNullValue()));
        assertThat(link.getIdentifier(), is("dataset.person"));
        assertThat(link.getUri(), is("/gdc/md/PROJECT_ID/ldm/singleloadinterface/dataset.person"));
        assertThat(link.getCategory(), is("dataset-singleloadinterface"));
        assertThat(link.getTitle(), is("Person"));
        assertThat(link.getSummary(), is("dataset single data loading interface specifications"));
    }
}