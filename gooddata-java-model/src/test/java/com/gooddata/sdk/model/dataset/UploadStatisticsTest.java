/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataset;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import org.testng.annotations.Test;

public class UploadStatisticsTest {

    @Test
    public void shouldDeserialize() throws Exception {
        final UploadStatistics uploadStatistics = readObjectFromResource("/dataset/uploads/data-uploads-info.json", UploadStatistics.class);

        assertThat(uploadStatistics, notNullValue());

        assertThat(uploadStatistics.getUploadsCount("OK"), is(845));
        assertThat(uploadStatistics.getUploadsCount("ERROR"), is(25));
        assertThat(uploadStatistics.getUploadsCount("RUNNING"), is(1));
        assertThat(uploadStatistics.getUploadsCount("NOT_DEFINED"), is(0));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final UploadStatistics uploadStatistics = readObjectFromResource("/dataset/uploads/data-uploads-info.json", UploadStatistics.class);

        assertThat(uploadStatistics.toString(), matchesPattern(UploadStatistics.class.getSimpleName() + "\\[.*\\]"));
    }
}