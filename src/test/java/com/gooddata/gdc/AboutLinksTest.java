/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class AboutLinksTest {

    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/dataset/datasetLinks.json");
        final AboutLinks aboutLinks = new ObjectMapper().readValue(stream, AboutLinks.class);
        assertThat(aboutLinks, is(notNullValue()));
        assertThat(aboutLinks.getCategory(), is("singleloadinterface"));
        assertThat(aboutLinks.getInstance(), is("MD::LDM::SingleLoadInterface"));
        assertThat(aboutLinks.getSummary(), is("single loading interfaces"));

        final Collection<AboutLinks.Link> links = aboutLinks.getLinks();
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