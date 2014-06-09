package com.gooddata.report;

/**
 * Format of exported report
 */
public enum ReportExportFormat {

    PDF, XLS, PNG, CSV;

    public String getValue() {
        return name().toLowerCase();
    }

}
