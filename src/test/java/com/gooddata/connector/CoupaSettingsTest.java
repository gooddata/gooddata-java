/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class CoupaSettingsTest {

    @Test
    public void shouldSerialize() throws Exception {
        final CoupaSettings settings = new CoupaSettings("UTC");
        assertThat(settings, serializesToJson("/connector/settings-coupa.json"));
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