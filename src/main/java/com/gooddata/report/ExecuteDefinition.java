/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.report;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.util.GoodDataToStringBuilder;

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

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
