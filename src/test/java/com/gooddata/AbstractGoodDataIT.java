/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import com.gooddata.authentication.LoginPasswordAuthentication;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.port;

public abstract class AbstractGoodDataIT {

    protected GoodData gd;

    @BeforeMethod
    public void commonSetUp() {
        initJadler().that().respondsWithDefaultContentType("application/json");
        gd = new GoodData(
                new GoodDataEndpoint("localhost", port(), "http"),
                new LoginPasswordAuthentication("sdk@gooddata.com", "sdk"),
                createGoodDataSettings()
        );
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
