/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.report;

import org.testng.annotations.Test;

import java.util.Arrays;

import static com.gooddata.sdk.model.report.ReportExportFormat.toExportFormat;

@SuppressWarnings("deprecation")
public class ReportExportFormatTest {

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnNullInput() throws Exception {
        toExportFormat(null);
    }

    @Test
    public void shouldConvertAllValues() throws Exception {
        Arrays.stream(ReportExportFormat.values())
                .forEach(ReportExportFormat::toExportFormat);
    }
}