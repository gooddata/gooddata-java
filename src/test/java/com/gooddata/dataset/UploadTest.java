/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.InputStream;

public class UploadTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final InputStream input = getClass().getResourceAsStream("/dataset/uploads/upload.json");
        final Upload upload = new ObjectMapper().readValue(input, Upload.class);

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
}