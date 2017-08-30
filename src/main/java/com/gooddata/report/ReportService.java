/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.report;

import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.export.ExportService;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import org.springframework.web.client.RestTemplate;

import java.io.OutputStream;

import static com.gooddata.report.ReportExportFormat.toExportFormat;
import static com.gooddata.util.Validate.notNull;

/**
 * Service for report export
 * @deprecated use {@link com.gooddata.export.ExportService}
 */
@Deprecated
public class ReportService extends AbstractService {

    /**
     * @deprecated user {@link ExportService#EXPORTING_URI}
     */
    @Deprecated
    public static final String EXPORTING_URI = ExportService.EXPORTING_URI;

    private final ExportService service;

    public ReportService(final ExportService service, final RestTemplate restTemplate) {
        super(restTemplate);
        this.service = notNull(service, "service");
    }

    /**
     * Export the given report definition in the given format to the given output stream
     * @param reportDefinition report definition
     * @param format export format
     * @param output target
     * @return polling result
     * @throws NoDataReportException in case report contains no data
     * @throws ReportException on error
     * @deprecated use {@link ExportService#export(ReportDefinition, com.gooddata.export.ExportFormat, OutputStream)}
     */
    @Deprecated
    public FutureResult<Void> exportReport(final ReportDefinition reportDefinition, final ReportExportFormat format,
                                           final OutputStream output) {
        return service.export(reportDefinition, toExportFormat(format), output);
    }

    /**
     * Export the given report in the given format to the given output stream
     * @param report report
     * @param format export format
     * @param output target
     * @return polling result
     * @throws NoDataReportException in case report contains no data
     * @throws ReportException on error
     * @deprecated use {@link ExportService#export(Report, com.gooddata.export.ExportFormat, OutputStream)}
     */
    @Deprecated
    public FutureResult<Void> exportReport(final Report report, final ReportExportFormat format,
                                           final OutputStream output) {
        return service.export(report, toExportFormat(format), output);
    }

}
