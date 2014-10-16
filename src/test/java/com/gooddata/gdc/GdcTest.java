package com.gooddata.gdc;

import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class GdcTest {

    @Test
    public void deserialize() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/gdc/gdc.json");
        final Gdc gdc = new ObjectMapper().readValue(stream, Gdc.class);
        assertThat(gdc, is(notNullValue()));

        assertThat(gdc.getHomeLink(), is("/gdc/"));
        assertThat(gdc.getTokenLink(), is("/gdc/account/token"));
        assertThat(gdc.getLoginLink(), is("/gdc/account/login"));
        assertThat(gdc.getMetadataLink(), is("/gdc/md"));
        assertThat(gdc.getXTabLink(), is("/gdc/xtab2"));
        assertThat(gdc.getAvailableElementsLink(), is("/gdc/availableelements"));
        assertThat(gdc.getReportExporterLink(), is("/gdc/exporter"));
        assertThat(gdc.getAccountLink(), is("/gdc/account"));
        assertThat(gdc.getProjectsLink(), is("/gdc/projects"));
        assertThat(gdc.getToolLink(), is("/gdc/tool"));
        assertThat(gdc.getTemplatesLink(), is("/gdc/templates"));
        assertThat(gdc.getReleaseInfoLink(), is("/gdc/releaseInfo"));
        assertThat(gdc.getUserStagingLink(), is("{STAGING_LINK}"));
    }
}