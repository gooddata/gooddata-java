/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ServiceTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Service service = readObjectFromResource("/md/service-timezone.json", Service.class);

        assertThat(service, is(notNullValue()));
        assertThat(service.getTimezone(), is("UTC"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Service service = readObjectFromResource("/md/service-timezone.json", Service.class);

        assertThat(service.toString(), is("Service[timezone=UTC]"));
    }

}