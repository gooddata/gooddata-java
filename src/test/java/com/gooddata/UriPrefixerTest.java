/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata;

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
    public void testMergeUrisWithoutSlash() throws Exception {
        final UriPrefixer prefixer = new UriPrefixer("http://localhost:1/uploads");
        final URI result = prefixer.mergeUris("test");
        assertThat(result.toString(), is("http://localhost:1/uploads/test"));
    }
}