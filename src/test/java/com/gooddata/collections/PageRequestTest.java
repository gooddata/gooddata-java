/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.collections;

import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PageRequestTest {

    @Test
    public void testGetPageUri() throws Exception {
        final PageRequest pageRequest = new PageRequest(12, 10);
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("test_uri");
        final URI pageUri = pageRequest.getPageUri(uriBuilder);
        assertThat(pageUri, notNullValue());
        assertThat(pageUri.toString(), is("test_uri?offset=12&limit=10"));
    }

    @Test
    public void testGetPageUriWithStringOffset() throws Exception {
        final PageRequest pageRequest = new PageRequest("17", 10);
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("test_uri");
        final URI pageUri = pageRequest.getPageUri(uriBuilder);
        assertThat(pageUri, notNullValue());
        assertThat(pageUri.toString(), is("test_uri?offset=17&limit=10"));
    }

    @Test
    public void testUpdateWithPageParams() throws Exception {
        final PageRequest pageRequest = new PageRequest(12, 10);
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("test_uri/{test}");
        final String pageUri = pageRequest.updateWithPageParams(uriBuilder).build().toUriString();
        assertThat(pageUri, is("test_uri/{test}?offset=12&limit=10"));
    }

    @Test
    public void testUpdateWithPageParamsWithStringOffset() throws Exception {
        final PageRequest pageRequest = new PageRequest("17", 10);
        final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("test_uri/{test}");
        final String pageUri = pageRequest.updateWithPageParams(uriBuilder).build().toUriString();
        assertThat(pageUri, is("test_uri/{test}?offset=17&limit=10"));
    }
}
