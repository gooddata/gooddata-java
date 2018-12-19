/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.connector;

import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CoupaSettingsTest {

    @Test
    public void shouldSerialize() throws Exception {
        final CoupaSettings settings = new CoupaSettings("UTC");
        assertThat(settings, jsonEquals(resource("connector/settings-coupa.json")));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final CoupaSettings settings = readObjectFromResource("/connector/settings-coupa.json", CoupaSettings.class);

        assertThat(settings.getTimeZone(), is("UTC"));
    }

    @Test
    public void shouldGetCoupaConnectorType() throws Exception {
        final CoupaSettings settings = new CoupaSettings("UTC");

        assertThat(settings.getConnectorType(), is(ConnectorType.COUPA));
    }
}