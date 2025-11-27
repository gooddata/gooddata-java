/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class GoodDataEndpointTest {

    private static final String HOST = "HOST";
    private static final int PORT = 1;
    private static final String PROTOCOL = "http";

    private GoodDataEndpoint endpoint;

    @BeforeMethod
    public void setUp() throws Exception {
        endpoint = new GoodDataEndpoint(HOST, PORT, PROTOCOL);
    }

    @Test
    public void testToUri() {
        assertThat(endpoint.toUri(), is("http://HOST:1"));
    }

}
