/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.authentication;

import com.gooddata.GoodDataEndpoint;
import com.gooddata.http.client.GoodDataHttpClient;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LoginPasswordAuthenticationTest {

    private static final String PASSWORD = "PASSWORD";
    private static final String LOGIN = "LOGIN";

    private LoginPasswordAuthentication loginPasswordAuthentication;

    @BeforeMethod
    public void setUp() throws Exception {
        loginPasswordAuthentication = new LoginPasswordAuthentication(LOGIN, PASSWORD);
    }

    @Test
    public void shouldCreateHttpClient() {
        final HttpClientBuilder clientBuilder = mock(HttpClientBuilder.class);
        when(clientBuilder.build()).thenReturn(mock(CloseableHttpClient.class));

        final HttpClient httpClient = loginPasswordAuthentication.createHttpClient(new GoodDataEndpoint("host", 1, "http"), clientBuilder);

        assertThat(httpClient, notNullValue());
        assertThat(httpClient, is(instanceOf(GoodDataHttpClient.class)));
    }

}
