/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md.report;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.report.ReportExportFormat;
import com.gooddata.report.ReportService;
import org.testng.annotations.Test;

/**
 * Report acceptance tests.
 */
public class ReportServiceAT extends AbstractGoodDataAT {

    @Test(groups = "report", dependsOnGroups = "dataset")
    public void exportReportDefinition() throws Exception {
        final ReportService reportService = gd.getReportService();
        reportService.exportReport(reportDefinition, ReportExportFormat.CSV, System.out);
    }

}
