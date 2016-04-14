/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class RootLinksTest {

    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/gdc/gdc.json");
        final RootLinks rootLinks = new ObjectMapper().readValue(stream, RootLinks.class);
        assertThat(rootLinks, is(notNullValue()));

        assertThat(rootLinks.getHomeLink(), is("/gdc/"));
        assertThat(rootLinks.getTokenLink(), is("/gdc/account/token"));
        assertThat(rootLinks.getLoginLink(), is("/gdc/account/login"));
        assertThat(rootLinks.getMetadataLink(), is("/gdc/md"));
        assertThat(rootLinks.getXTabLink(), is("/gdc/xtab2"));
        assertThat(rootLinks.getAvailableElementsLink(), is("/gdc/availableelements"));
        assertThat(rootLinks.getReportExporterLink(), is("/gdc/exporter"));
        assertThat(rootLinks.getAccountLink(), is("/gdc/account"));
        assertThat(rootLinks.getProjectsLink(), is("/gdc/projects"));
        assertThat(rootLinks.getToolLink(), is("/gdc/tool"));
        assertThat(rootLinks.getTemplatesLink(), is("/gdc/templates"));
        assertThat(rootLinks.getReleaseInfoLink(), is("/gdc/releaseInfo"));
        assertThat(rootLinks.getUserStagingLink(), is("/uploads"));
    }
}