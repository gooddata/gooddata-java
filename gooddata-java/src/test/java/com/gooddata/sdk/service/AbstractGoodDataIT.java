/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static net.jadler.Jadler.*;

public abstract class AbstractGoodDataIT {

    protected GoodData gd;
    protected GoodDataEndpoint endpoint;

    @BeforeMethod
    public void commonSetUp() {
        initJadler().withDefaultResponseContentType("application/json");
        endpoint = new GoodDataEndpoint("localhost", port(), "http");
        gd = new GoodData(
                new LoginPasswordGoodDataRestProvider(
                        endpoint,
                        createGoodDataSettings(),
                        "sdk@gooddata.com",
                        "sdk"
                ));
    }

    protected GoodDataSettings createGoodDataSettings() {
        final GoodDataSettings settings = new GoodDataSettings();
        settings.setPollSleep(0);
        return settings;
    }

    @AfterMethod
    public void tearDown() {
        closeJadler();
    }
}
