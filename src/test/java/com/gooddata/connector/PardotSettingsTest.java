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

public class PardotSettingsTest {

    @Test
    public void shouldSerialize() throws Exception {
        final PardotSettings settings = new PardotSettings("someAccountId");

        assertThat(settings, serializesToJson("/connector/settings-pardot.json"));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final PardotSettings settings = readObjectFromResource("/connector/settings-pardot.json", PardotSettings.class);

        assertThat(settings.getAccountId(), is("someAccountId"));
    }
}