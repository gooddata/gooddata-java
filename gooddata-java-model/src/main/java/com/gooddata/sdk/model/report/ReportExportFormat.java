/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.report;

import com.gooddata.sdk.model.export.ExportFormat;

import static com.gooddata.util.Validate.notNull;

/**
 * Format of exported report
 *
 * @deprecated use {@link ExportFormat}
 */
@Deprecated
public enum ReportExportFormat {

    PDF, XLS, PNG, CSV, HTML, XLSX;

    public String getValue() {
        return name().toLowerCase();
    }

    public static String[] arrayToStringArray(ReportExportFormat... formats) {
        String[] fs = new String[formats.length];
        for (int i = 0; i < formats.length; i++) {
            fs[i] = formats[i].getValue();
        }
        return fs;
    }

    public static ExportFormat toExportFormat(final ReportExportFormat format) {
        notNull(format, "format");
        return ExportFormat.valueOf(format.name());
    }
}
