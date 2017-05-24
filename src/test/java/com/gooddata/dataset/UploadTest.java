/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
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
        assertThat(upload.getCreatedAt(), is(new DateTime(2016, 4, 8, 12, 55, 21, DateTimeZone.UTC)));
        assertThat(upload.getSize(), is(130501));
        assertThat(upload.getProcessedAt(), is(new DateTime(2016, 4, 8, 12, 55, 25, DateTimeZone.UTC)));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final Upload upload = readObjectFromResource("/dataset/uploads/upload.json", Upload.class);

        assertThat(upload.toString(), matchesPattern(Upload.class.getSimpleName() + "\\[.*\\]"));
    }
}