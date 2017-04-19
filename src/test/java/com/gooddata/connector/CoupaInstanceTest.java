/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.connector;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.testng.annotations.Test;

public class CoupaInstanceTest {

    @Test
    public void shouldSerialize() throws Exception {
        final CoupaInstance instance =
                new CoupaInstance("instance 01", "https://gooddata-demo.coupacloud.com/api", "apikey123");

        assertThat(instance, jsonEquals(resource("connector/coupa_instance.json")));
    }

    @Test
    public void shouldDeserialize() throws Exception {
        final CoupaInstance instance = readObjectFromResource("/connector/coupa_instance.json", CoupaInstance.class);

        assertThat(instance.getName(), is("instance 01"));
        assertThat(instance.getApiUrl(), is("https://gooddata-demo.coupacloud.com/api"));
        assertThat(instance.getApiKey(), is("apikey123"));
    }
}