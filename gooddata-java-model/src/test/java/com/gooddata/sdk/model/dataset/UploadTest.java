/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.time.ZoneOffset.UTC;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class UploadTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final Upload upload = readObjectFromResource("/dataset/uploads/upload.json", Upload.class);

        assertThat(upload, notNullValue());
        assertThat(upload.getMessage(), is("some upload message"));
        assertThat(upload.getProgress(), closeTo(1, 0.0001));
        assertThat(upload.getStatus(), is("OK"));
        assertThat(upload.getUploadMode(), is(UploadMode.INCREMENTAL));
        assertThat(upload.getUri(), is("/gdc/md/project/data/upload/123"));
        assertThat(upload.getCreatedAt(), is(LocalDateTime.of(2016, 4, 8, 12, 55, 21).atZone(UTC)));
        assertThat(upload.getSize(), is(130501));
        assertThat(upload.getProcessedAt(), is(LocalDateTime.of(2016, 4, 8, 12, 55, 25).atZone(UTC)));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Upload upload = readObjectFromResource("/dataset/uploads/upload.json", Upload.class);

        assertThat(upload.toString(), matchesPattern(Upload.class.getSimpleName() + "\\[.*\\]"));
    }
}