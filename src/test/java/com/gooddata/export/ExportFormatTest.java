/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import org.testng.annotations.Test;

import static com.gooddata.export.ExportFormat.arrayToStringArray;
import static com.gooddata.report.ReportExportFormat.toExportFormat;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;

public class ExportFormatTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnNullInput() throws Exception {
        toExportFormat(null);
    }

    @Test
    public void shouldConvert() throws Exception {
        assertThat(arrayToStringArray(ExportFormat.CSV, ExportFormat.PDF), arrayContaining("csv", "pdf"));
    }
}