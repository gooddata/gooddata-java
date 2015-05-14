package com.gooddata.report;

/**
 * Thrown when report export contains no data
 */
public class NoDataReportException extends ReportException {

    public NoDataReportException() {
        this("Report contains no data");
    }

    public NoDataReportException(final String message) {
        super(message);
    }

}
