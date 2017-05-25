/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.connector.ConnectorType.ZENDESK4;
import static com.gooddata.connector.Zendesk4Settings.Zendesk4Type.plus;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class Zendesk4SettingsTest {

    @Test
    public void shouldSerialize() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("https://foo.com", plus.name(), "12am", "America/Los_Angeles");
        assertThat(settings, serializesToJson("/connector/settings-zendesk4.json"));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final Zendesk4Settings settings = new ObjectMapper()
                .readValue(getClass().getResource("/connector/settings-zendesk4.json"), Zendesk4Settings.class);

        assertThat(settings, is(notNullValue()));
        assertThat(settings.getApiUrl(), is("https://foo.com"));
        assertThat(settings.getType(), is(plus.name()));
        assertThat(settings.getSyncTime(), is("12am"));
        assertThat(settings.getSyncTimeZone(), is("America/Los_Angeles"));
    }

    @Test
    public void testGetConnectorType() throws Exception {
        assertThat(new Zendesk4Settings("url").getConnectorType(), is(ZENDESK4));
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testSetApiUrlWithEmptyValue() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("old url");
        settings.setApiUrl("");
    }

    @Test(expectedExceptions = {IllegalArgumentException.class})
    public void testSetApiUrlWithNull() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("old url");
        settings.setApiUrl(null);
    }

    @Test
    public void testSetApiUrl() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("old url");
        settings.setApiUrl("new url");
        assertThat(settings.getApiUrl(), is("new url"));
    }

    @Test
    public void testToStringFormat() {
        final Zendesk4Settings settings = new Zendesk4Settings("old url");

        assertThat(settings.toString(), matchesPattern(Zendesk4Settings.class.getSimpleName() + "\\[.*\\]"));
    }
}