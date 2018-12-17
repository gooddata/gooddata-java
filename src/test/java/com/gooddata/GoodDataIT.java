/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.apache.http.HttpHeaders;
import org.testng.annotations.Test;

import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.verifyThatRequest;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.startsWith;

public class GoodDataIT extends AbstractGoodDataIT {

    private static final String USER_AGENT = "customAgent/X.Y";

    @Override
    protected GoodDataSettings createGoodDataSettings() {
        final GoodDataSettings settings = super.createGoodDataSettings();
        settings.setUserAgent(USER_AGENT);
        return settings;
    }

    @Test
    public void shouldSentCustomUserAgentHeader() {
        onRequest()
                .havingMethodEqualTo("GET")
            .respond()
                .withStatus(200);

        gd.getGdcService().getRootLinks();

        verifyThatRequest()
                .havingHeader(HttpHeaders.USER_AGENT, hasItem(startsWith(USER_AGENT + " GoodData-Java-SDK")))
                .receivedOnce();
    }
}
