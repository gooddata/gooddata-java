package com.gooddata.report;


import org.codehaus.jackson.annotate.JsonProperty;

import static com.gooddata.util.Validate.notNull;

class ExecuteDefinition extends ReportRequest {

    private final String reportDefinitionUri;

    public ExecuteDefinition(final String reportDefinitionUri) {
        this.reportDefinitionUri = notNull(reportDefinitionUri, "reportDefinitionUri");
    }

    @JsonProperty("reportDefinition")
    public String getReportDefinitionUri() {
        return reportDefinitionUri;
    }
}
