/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.export;

import org.testng.annotations.Test;

import static com.gooddata.sdk.model.export.ExportFormat.arrayToStringArray;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContaining;

public class ExportFormatTest {

    @Test
    public void shouldConvert() {
        assertThat(arrayToStringArray(ExportFormat.CSV, ExportFormat.PDF), arrayContaining("csv", "pdf"));
    }
}