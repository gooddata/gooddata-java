package com.gooddata.report;


import com.fasterxml.jackson.annotation.JsonProperty;

import static com.gooddata.util.Validate.notNull;

class ExecuteReport extends ReportRequest {

    private final String reportUri;

    public ExecuteReport(final String reportUri) {
        this.reportUri = notNull(reportUri, "reportUri");
    }

    @JsonProperty("report")
    public String getReportUri() {
        return reportUri;
    }
}
