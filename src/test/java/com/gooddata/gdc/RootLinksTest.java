/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RootLinksTest {

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/gdc/gdc.json");
        final RootLinks rootLinks = new ObjectMapper().readValue(stream, RootLinks.class);
        assertThat(rootLinks, is(notNullValue()));

        assertThat(rootLinks.getHomeLink(), is("/gdc/"));
        assertThat(rootLinks.getHomeUri(), is("/gdc/"));
        assertThat(rootLinks.getTokenLink(), is("/gdc/account/token"));
        assertThat(rootLinks.getTokenUri(), is("/gdc/account/token"));
        assertThat(rootLinks.getLoginLink(), is("/gdc/account/login"));
        assertThat(rootLinks.getLoginUri(), is("/gdc/account/login"));
        assertThat(rootLinks.getMetadataLink(), is("/gdc/md"));
        assertThat(rootLinks.getMetadataUri(), is("/gdc/md"));
        assertThat(rootLinks.getXTabLink(), is("/gdc/xtab2"));
        assertThat(rootLinks.getXTabUri(), is("/gdc/xtab2"));
        assertThat(rootLinks.getAvailableElementsLink(), is("/gdc/availableelements"));
        assertThat(rootLinks.getAvailableElementsUri(), is("/gdc/availableelements"));
        assertThat(rootLinks.getReportExporterLink(), is("/gdc/exporter"));
        assertThat(rootLinks.getReportExporterUri(), is("/gdc/exporter"));
        assertThat(rootLinks.getAccountLink(), is("/gdc/account"));
        assertThat(rootLinks.getAccountUri(), is("/gdc/account"));
        assertThat(rootLinks.getProjectsLink(), is("/gdc/projects"));
        assertThat(rootLinks.getProjectsUri(), is("/gdc/projects"));
        assertThat(rootLinks.getToolLink(), is("/gdc/tool"));
        assertThat(rootLinks.getToolUri(), is("/gdc/tool"));
        assertThat(rootLinks.getTemplatesLink(), is("/gdc/templates"));
        assertThat(rootLinks.getTemplatesUri(), is("/gdc/templates"));
        assertThat(rootLinks.getReleaseInfoLink(), is("/gdc/releaseInfo"));
        assertThat(rootLinks.getReleaseInfoUri(), is("/gdc/releaseInfo"));
        assertThat(rootLinks.getUserStagingLink(), is("/uploads"));
        assertThat(rootLinks.getUserStagingUri(), is("/uploads"));
    }
}