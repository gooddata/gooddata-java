/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;

/**
 * Format of exported report
 */
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
}
