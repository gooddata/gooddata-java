/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.export;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.export.*;
import com.gooddata.sdk.model.gdc.AsyncTask;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.md.AbstractObj;
import com.gooddata.sdk.model.md.Obj;
import com.gooddata.sdk.model.md.ProjectDashboard;
import com.gooddata.sdk.model.md.ProjectDashboard.Tab;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.io.OutputStream;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Export project data
 *
 */
public class ExportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    private static final String CLIENT_EXPORT_URI = "/gdc/projects/{projectId}/clientexport";

    private static final String RAW_EXPORT_URI = "/gdc/projects/{projectId}/execute/raw";

    public static final UriTemplate OBJ_TEMPLATE = new UriTemplate(Obj.OBJ_URI);
    public static final UriTemplate PROJECT_TEMPLATE = new UriTemplate(Project.URI);

    /**
     * Service for data export
     * @param restTemplate REST template
     * @param settings settings
     */
    public ExportService(final RestTemplate restTemplate, final GoodDataSettings settings) {
        super(restTemplate, settings);
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
        final JsonNode execResult = executeReport(ReportRequest.URI, request);
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

    protected JsonNode executeReport(final String executionUri, final ReportRequest request) {
        try {
            final ResponseEntity<String> entity = restTemplate
                    .exchange(executionUri, POST, new HttpEntity<>(request), String.class);
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
            return notNullState(restTemplate.postForObject(EXPORTING_URI, root, UriResponse.class), "exported report").getUri();
        } catch (GoodDataException | RestClientException e) {
            throw new ExportException("Unable to export report", e);
        }
    }

    /**
     * Export the given dashboard tab in PDF format to the given output stream
     *
     * @param endpoint  endpoint for which the export is generated
     * @param dashboard dashboard
     * @param tab       tab
     * @param output    output
     * @return polling result
     * @throws ExportException if export fails
     */
    public FutureResult<Void> exportPdf(final GoodDataEndpoint endpoint, final ProjectDashboard dashboard, final Tab tab, final OutputStream output) {
        notNull(endpoint, "endpoint");
        notNull(dashboard, "dashboard");
        notNull(tab, "tab");
        notNull(output, "output");

        final String projectId = extractProjectId(dashboard);
        final String projectUri = PROJECT_TEMPLATE.expand(projectId).toString();
        final String dashboardUri = dashboard.getUri();

        final ClientExport export = new ClientExport(endpoint.toUri(), projectUri, dashboardUri, tab.getIdentifier());
        final AsyncTask task;
        try {
            task = restTemplate.postForObject(CLIENT_EXPORT_URI, export, AsyncTask.class, projectId);
        } catch (RestClientException | GoodDataRestException e) {
            throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(notNullState(task, "export pdf task").getUri(), Void.class) {
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

    /**
     * Export the given Report using the raw export (without columns/rows limitations)
     * @param report report
     * @param output output
     * @return polling result
     * @throws ExportException in case export fails
     */
    public FutureResult<Void> exportCsv(final Report report, final OutputStream output) {
        notNull(report, "report");
        return exportCsv(report, new ExecuteReport(report), output);
    }

    /**
     * Export the given Report Definition using the raw export (without columns/rows limitations)
     * @param definition report definition
     * @param output output
     * @return polling result
     * @throws ExportException in case export fails
     */
    public FutureResult<Void> exportCsv(final ReportDefinition definition, final OutputStream output) {
        final ReportRequest request = new ExecuteReportDefinition(definition);
        return exportCsv(definition, request, output);
    }

    private FutureResult<Void> exportCsv(final AbstractObj obj, final ReportRequest request, final OutputStream output) {
        notNull(obj, "obj");
        notNull(request, "request");
        notNull(output, "output");

        final String projectId = extractProjectId(obj);
        final String uri = obj.getUri();

        final UriResponse response;
        try {
            response = restTemplate.postForObject(RAW_EXPORT_URI, request, UriResponse.class, projectId);
        } catch (RestClientException | GoodDataRestException e) {
            throw new ExportException("Unable to export: " + uri);
        }
        if (response == null || response.getUri() == null) {
            throw new ExportException("Empty response, unable to export: " + uri);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(response.getUri(), Void.class) {
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
                        throw new ExportException("Unable to export: " + uri +
                                ", unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(getPolling(), GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ExportException("Unable to export: " + uri, e);
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export: " + uri, e);
            }
        });
    }

    static String extractProjectId(final AbstractObj obj) {
        notNull(obj, "obj");
        notNull(obj.getUri(), "obj.uri");

        final String projectId = OBJ_TEMPLATE.match(obj.getUri()).get("projectId");
        notNull(projectId, "obj uri - project id");
        return projectId;
    }
}
