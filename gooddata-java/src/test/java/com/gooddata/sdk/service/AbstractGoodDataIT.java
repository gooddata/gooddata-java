/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import com.gooddata.sdk.service.httpcomponents.LoginPasswordGoodDataRestProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static net.jadler.Jadler.*;

public abstract class AbstractGoodDataIT {

    protected GoodData gd;
    protected GoodDataEndpoint endpoint;

    @BeforeEach
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

    @AfterEach
    public void tearDown() {
        closeJadler();
    }
}
