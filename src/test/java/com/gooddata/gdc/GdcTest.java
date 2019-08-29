/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GdcTest {

    @SuppressWarnings("deprecation")
    @Test
    public void deserialize() throws Exception {
        final Gdc gdc = readObjectFromResource("/gdc/gdc.json", Gdc.class);
        assertThat(gdc, is(notNullValue()));

        assertThat(gdc.getHomeLink(), is("/gdc/"));
        assertThat(gdc.getHomeUri(), is("/gdc/"));
        assertThat(gdc.getTokenLink(), is("/gdc/account/token"));
        assertThat(gdc.getTokenUri(), is("/gdc/account/token"));
        assertThat(gdc.getLoginLink(), is("/gdc/account/login"));
        assertThat(gdc.getLoginUri(), is("/gdc/account/login"));
        assertThat(gdc.getMetadataLink(), is("/gdc/md"));
        assertThat(gdc.getMetadataUri(), is("/gdc/md"));
        assertThat(gdc.getXTabLink(), is("/gdc/xtab2"));
        assertThat(gdc.getXTabUri(), is("/gdc/xtab2"));
        assertThat(gdc.getAvailableElementsLink(), is("/gdc/availableelements"));
        assertThat(gdc.getAvailableElementsUri(), is("/gdc/availableelements"));
        assertThat(gdc.getReportExporterLink(), is("/gdc/exporter"));
        assertThat(gdc.getReportExporterUri(), is("/gdc/exporter"));
        assertThat(gdc.getAccountLink(), is("/gdc/account"));
        assertThat(gdc.getAccountUri(), is("/gdc/account"));
        assertThat(gdc.getProjectsLink(), is("/gdc/projects"));
        assertThat(gdc.getProjectsUri(), is("/gdc/projects"));
        assertThat(gdc.getToolLink(), is("/gdc/tool"));
        assertThat(gdc.getToolUri(), is("/gdc/tool"));
        assertThat(gdc.getTemplatesLink(), is("/gdc/templates"));
        assertThat(gdc.getTemplatesUri(), is("/gdc/templates"));
        assertThat(gdc.getUserStagingLink(), is("/uploads"));
        assertThat(gdc.getUserStagingUri(), is("/uploads"));
    }
}