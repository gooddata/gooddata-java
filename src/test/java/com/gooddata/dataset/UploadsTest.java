/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import java.io.InputStream;

public class UploadsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/uploads.json");
        final Uploads uploads = new ObjectMapper().readValue(input, Uploads.class);

        assertThat(uploads, notNullValue());
        assertThat(uploads.items(), not(Matchers.empty()));
        assertThat(uploads.items().size(), is(2));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/uploads.json");
        final Uploads uploads = new ObjectMapper().readValue(input, Uploads.class);

        assertThat(uploads.toString(), matchesPattern(Uploads.class.getSimpleName() + "\\[.*\\]"));
    }
}
