/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.model.gdc.Gdc;
import com.gooddata.sdk.model.gdc.RootLinks;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class GdcServiceIT extends AbstractGoodDataIT {

    @SuppressWarnings("deprecation")
    @Test
    public void shouldReturnGdc() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
            .respond()
                .withBody(readFromResource("/gdc/gdc.json"))
                .withStatus(200);

        final Gdc gdc = gd.getGdcService().getGdc();
        assertThat(gdc, is(notNullValue()));
        assertThat(gdc.getUserStagingUri(), is("/uploads"));
    }

    @Test
    public void shouldReturnRootLinks() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
            .respond()
                .withBody(readFromResource("/gdc/gdc.json"))
                .withStatus(200);

        final RootLinks rootLinks = gd.getGdcService().getRootLinks();
        assertThat(rootLinks, is(notNullValue()));
        assertThat(rootLinks.getUserStagingUri(), is("/uploads"));
    }

}
