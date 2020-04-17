/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import org.testng.annotations.Test;

import static com.gooddata.sdk.model.connector.ConnectorType.ZENDESK4;
import static com.gooddata.sdk.model.connector.Zendesk4Settings.Zendesk4Type.plus;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class Zendesk4SettingsTest {

    @Test
    public void shouldSerialize() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("https://foo.com", "zopim.com", "testAccount",
                plus.name(), "12am", "America/Los_Angeles");
        assertThat(settings, jsonEquals(resource("connector/settings-zendesk4.json")));
    }

    @Test
    public void shouldSerialize_beforeTemplate26() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("https://foo.com", null, null,
                plus.name(), "12am", "America/Los_Angeles");
        assertThat(settings, jsonEquals(resource("connector/settings-zendesk4-before-template26.json")));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final Zendesk4Settings settings = readObjectFromResource("/connector/settings-zendesk4.json", Zendesk4Settings.class);

        assertThat(settings, is(notNullValue()));
        assertThat(settings.getApiUrl(), is("https://foo.com"));
        assertThat(settings.getZopimUrl(), is("zopim.com"));
        assertThat(settings.getAccount(), is("testAccount"));
        assertThat(settings.getType(), is(plus.name()));
        assertThat(settings.getSyncTime(), is("12am"));
        assertThat(settings.getSyncTimeZone(), is("America/Los_Angeles"));
    }

    @Test
    public void shouldDeserialize_beforeTemplate26() throws Exception {
        final Zendesk4Settings settings = readObjectFromResource("/connector/settings-zendesk4-before-template26.json", Zendesk4Settings.class);

        assertThat(settings, is(notNullValue()));
        assertThat(settings.getApiUrl(), is("https://foo.com"));
        assertThat(settings.getZopimUrl(), is(nullValue()));
        assertThat(settings.getAccount(), is(nullValue()));
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
    public void testSetZopimUrl() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("api url");
        settings.setZopimUrl("zopim url");
        assertThat(settings.getZopimUrl(), is("zopim url"));
    }

    @Test
    public void testSetAccount() throws Exception {
        final Zendesk4Settings settings = new Zendesk4Settings("api url");
        settings.setAccount("test account");
        assertThat(settings.getAccount(), is("test account"));
    }

    @Test
    public void testToStringFormat() {
        final Zendesk4Settings settings = new Zendesk4Settings("old url");

        assertThat(settings.toString(), matchesPattern(Zendesk4Settings.class.getSimpleName() + "\\[.*\\]"));
    }
}