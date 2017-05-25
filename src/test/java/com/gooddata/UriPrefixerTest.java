/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata;

import org.springframework.web.util.UriComponentsBuilder;
import org.testng.annotations.Test;

import java.net.URI;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UriPrefixerTest {

    @Test
    public void testMergeUrisWithSlash() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads");
        final URI result = prefixer.mergeUris("/test");
        assertThat(result.toString(), is("http://localhost:1/uploads/test"));
    }

    @Test
    public void testMergeUrisWithSlashes() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads/");
        final URI result = prefixer.mergeUris("/test");
        assertThat(result.toString(), is("http://localhost:1/uploads/test"));
    }

    @Test
    public void testMergeUrisWithAllSlashes() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads/");
        final URI result = prefixer.mergeUris("/test/");
        assertThat(result.toString(), is("http://localhost:1/uploads/test/"));
    }

    @Test
    public void testMergeUrisWithoutSlash() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads/");
        final URI result = prefixer.mergeUris("test");
        assertThat(result.toString(), is("http://localhost:1/uploads/test"));
    }

    @Test
    public void testMergeUrisWithoutSlashes() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads");
        final URI result = prefixer.mergeUris("test");
        assertThat(result.toString(), is("http://localhost:1/uploads/test"));
    }

    @Test
    public void shouldNotDoubleEncodeQueryParam() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads");

        final URI uri = UriComponentsBuilder.fromPath("/foo")
                .queryParam("bar", "\n") // some crazy value which needs encoding
                .build()
                .toUri();

        final URI result = prefixer.mergeUris(uri);
        assertThat(result.toString(), is("http://localhost:1/uploads/foo?bar=%0A"));
    }

    @Test
    public void shouldName() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("https://localhost:1");
        final URI result = prefixer.mergeUris("http://localhost:1/uploads/test/");
        assertThat(result.toString(), is("https://localhost:1/uploads/test/"));
    }
}