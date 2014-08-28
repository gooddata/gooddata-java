/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;

import static com.gooddata.Validate.notNull;
import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.port;

import java.io.InputStream;

public abstract class AbstractGoodDataIT {

    protected static final ObjectMapper MAPPER = new ObjectMapper();
    protected  GoodData gd;

    @Before
    public void commonSetUp() {
        initJadler().that().respondsWithDefaultContentType("application/json");
        gd = new GoodData("localhost", "sdk@gooddata.com", "sdk", port(), "http") {
            @Override
            protected HttpClient createHttpClient(final String login, final String password, final String hostname,
                                                  final int port, final String protocol, final HttpClientBuilder builder) {
                return builder.build();
            }
        };
    }

    @After
    public void tearDown() {
        closeJadler();
    }

    protected InputStream readResource(String path) {
        return notNull(getClass().getResourceAsStream(path), "resource denote by path: " + path);
    }
}
