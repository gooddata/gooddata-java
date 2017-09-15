/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.util.GoodDataToStringBuilder;

import static com.gooddata.util.Validate.notNull;

/**
 * Report definition execution request
 */
public class ExecuteReportDefinition extends ReportRequest {

    private final String reportDefinitionUri;

    ExecuteReportDefinition(final String reportDefinitionUri) {
        this.reportDefinitionUri = notNull(reportDefinitionUri, "reportDefinitionUri");
    }

    /**
     * Create ExecuteReportDefinition based on {@link ReportDefinition}
     *
     * @param reportDefinition to create report definition execution request for
     */
    public ExecuteReportDefinition(final ReportDefinition reportDefinition) {
        this(notNull(reportDefinition, "reportDefinition").getUri());
    }

    @JsonProperty("reportDefinition")
    public String getReportDefinitionUri() {
        return reportDefinitionUri;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
