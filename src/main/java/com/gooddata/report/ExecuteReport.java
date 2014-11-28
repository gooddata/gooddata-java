package com.gooddata.report;


import org.codehaus.jackson.annotate.JsonProperty;

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
