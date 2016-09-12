/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;


import com.fasterxml.jackson.annotation.JsonProperty;

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
