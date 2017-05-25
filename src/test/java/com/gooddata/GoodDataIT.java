/**
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import com.gooddata.authentication.LoginPasswordAuthentication;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.testng.annotations.Test;

import static net.jadler.Jadler.port;
import static net.jadler.Jadler.verifyThatRequest;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.startsWith;

public class GoodDataIT extends AbstractGoodDataIT {

    @Test
    public void shouldSentCustomUserAgentHeader() throws Exception {
        String userAgent = "customAgent/X.Y";
        GoodDataSettings goodDataSettings = new GoodDataSettings();
        goodDataSettings.setUserAgent(userAgent);
        GoodData goodData = new GoodData(new GoodDataEndpoint("localhost", port(), "http"),
                new LoginPasswordAuthentication("sdk@gooddata.com", "sdk"),
                goodDataSettings);
        URIBuilder uriBuilder = new URIBuilder().setScheme("http").setHost("localhost").setPort(port()).setPath("/testCustomUserAgent");
        goodData.getHttpClient().execute(new HttpGet(uriBuilder.build()));

        verifyThatRequest().havingPathEqualTo("/testCustomUserAgent")
                .havingHeader(HttpHeaders.USER_AGENT, hasItem(startsWith(userAgent + " GoodData-Java-SDK")))
                .receivedOnce();
    }
}
