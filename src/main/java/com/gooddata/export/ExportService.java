/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.GoodDataEndpoint;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.PollResult;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import com.gooddata.gdc.AsyncTask;
import com.gooddata.md.ProjectDashboard;
import com.gooddata.md.ProjectDashboard.Tab;
import com.gooddata.project.Project;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;

import static com.gooddata.util.Validate.notNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

import static com.gooddata.md.Obj.OBJ_TEMPLATE;
import static com.gooddata.util.Validate.notNull;
import static org.springframework.http.HttpMethod.GET;

/**
 * Export project data
 * @see com.gooddata.report.ReportService
 */
public class ExportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    private static final String CLIENT_EXPORT_URI = "/gdc/projects/{projectId}/clientexport";

    private final GoodDataEndpoint endpoint;

    /**
     * Service for data export
     * @param restTemplate REST template
     * @param endpoint GoodData Endpoint
     */
    public ExportService(final RestTemplate restTemplate, final GoodDataEndpoint endpoint) {
        super(restTemplate);
        this.endpoint = notNull(endpoint, "endpoint");
    }

    /**
     * Export the given report definition in the given format to the given output stream
     *
     * @param reportDefinition report definition
     * @param format           export format
     * @param output           target
     * @return polling result
     * @throws NoDataExportException in case report contains no data
     * @throws ExportException       on error
     */
    public FutureResult<Void> export(final ReportDefinition reportDefinition, final ExportFormat format,
                                     final OutputStream output) {
        notNull(reportDefinition, "reportDefinition");
        final ReportRequest request = new ExecuteReportDefinition(reportDefinition);
        return exportReport(request, format, output);
    }

    /**
     * Export the given report in the given format to the given output stream
     *
     * @param report report
     * @param format export format
     * @param output target
     * @return polling result
     * @throws NoDataExportException in case report contains no data
     * @throws ExportException       on error
     */
    public FutureResult<Void> export(final Report report, final ExportFormat format,
                                     final OutputStream output) {
        notNull(report, "report");
        final ReportRequest request = new ExecuteReport(report);
        return exportReport(request, format, output);
    }

    private FutureResult<Void> exportReport(final ReportRequest request, final ExportFormat format, final OutputStream output) {
        notNull(output, "output");
        notNull(format, "format");
        final JsonNode execResult = executeReport(request);
        final String uri = exportReport(execResult, format);
        return new PollResult<>(this, new SimplePollHandler<Void>(uri, Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                switch (response.getStatusCode()) {
                    case OK:
                        return true;
                    case ACCEPTED:
                        return false;
                    case NO_CONTENT:
                        throw new NoDataExportException();
                    default:
                        throw new ExportException("Unable to export report, unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export report", e);
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(uri, GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ExportException("Unable to export report", e);
                }
            }
        });
    }

    private JsonNode executeReport(final ReportRequest request) {
        try {
            final ResponseEntity<String> entity = restTemplate
                    .exchange(ReportRequest.URI, POST, new HttpEntity<>(request), String.class);
            return mapper.readTree(entity.getBody());
        } catch (GoodDataException | RestClientException e) {
            throw new ExportException("Unable to execute report", e);
        } catch (IOException e) {
            throw new ExportException("Unable to read execution result", e);
        }
    }

    private String exportReport(final JsonNode execResult, final ExportFormat format) {
        notNull(execResult, "execResult");
        notNull(format, "format");
        final ObjectNode root = mapper.createObjectNode();
        final ObjectNode child = mapper.createObjectNode();

        child.set("result", execResult);
        child.put("format", format.getValue());
        root.set("result_req", child);

        try {
            return restTemplate.postForObject(EXPORTING_URI, root, UriResponse.class).getUri();
        } catch (GoodDataException | RestClientException e) {
            throw new ExportException("Unable to export report", e);
        }
    }

    /**
     * Export the given dashboard tab in PDF format to the given output stream
     * @param dashboard dashboard
     * @param tab tab
     * @param output output
     * @return polling result
     * @throws ExportException if export fails
     */
    public FutureResult<Void> exportPdf(final ProjectDashboard dashboard, final Tab tab, final OutputStream output) {
        notNull(dashboard, "dashboard");
        notNull(tab, "tab");
        notNull(output, "output");

        final String projectId = extractProjectId(dashboard);
        final String projectUri = Project.TEMPLATE.expand(projectId).toString();
        final String dashboardUri = dashboard.getUri();

        final ClientExport export = new ClientExport(endpoint, projectUri, dashboardUri, tab.getIdentifier());
        final AsyncTask task;
        try {
            task = restTemplate.postForObject(CLIENT_EXPORT_URI, export, AsyncTask.class, projectId);
        } catch (RestClientException | GoodDataRestException e) {
            throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(task.getUri(), Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                switch (response.getStatusCode()) {
                    case OK:
                        return true;
                    case ACCEPTED:
                        return false;
                    default:
                        throw new ExportException("Unable to export dashboard: " + dashboardUri +
                                ", unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(task.getUri(), GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
            }
        });
    }

    static String extractProjectId(final ProjectDashboard dashboard) {
        notNull(dashboard, "dashboard");
        notNull(dashboard.getUri(), "dashboard.uri");

        final String projectId = OBJ_TEMPLATE.match(dashboard.getUri()).get("projectId");
        notNull(projectId, "dashboard uri - project id");
        return projectId;
    }}
