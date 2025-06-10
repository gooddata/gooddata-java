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
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.io.OutputStream;

import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;

/**
 * Export project data using WebClient (Spring 6, JDK 17)
 */
public class ExportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";
    private static final String CLIENT_EXPORT_URI = "/gdc/projects/{projectId}/clientexport";
    private static final String RAW_EXPORT_URI = "/gdc/projects/{projectId}/execute/raw";
    public static final UriTemplate OBJ_TEMPLATE = new UriTemplate(Obj.OBJ_URI);
    public static final UriTemplate PROJECT_TEMPLATE = new UriTemplate(Project.URI);

    /**
     * Service for data export
     * @param webClient WebClient
     * @param settings settings
     */
    public ExportService(final WebClient webClient, final GoodDataSettings settings) {
        super(webClient, settings);
    }

    /**
     * Export the given report definition in the given format to the given output stream
     */
    public FutureResult<Void> export(final ReportDefinition reportDefinition, final ExportFormat format,
                                     final OutputStream output) {
        notNull(reportDefinition, "reportDefinition");
        final ReportRequest request = new ExecuteReportDefinition(reportDefinition);
        return exportReport(request, format, output);
    }

    /**
     * Export the given report in the given format to the given output stream
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
            public boolean isFinished(ClientResponse response) {
                int code = response.statusCode().value();
                if (code == 200) {
                    return true;
                } else if (code == 202) {
                    return false;
                } else if (code == 204) {
                    throw new NoDataExportException();
                } else {
                    throw new ExportException("Unable to export report, unknown HTTP response code: " + code);
                }
            }

            @Override
            protected void onFinish() {
                // Download file using WebClient and write to OutputStream
                byte[] data = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block();
                if (data != null) {
                    try {
                        output.write(data);
                    } catch (IOException e) {
                        throw new ExportException("Unable to write export to output stream", e);
                    }
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ExportException("Unable to export report", e);
            }
        });
    }

    protected JsonNode executeReport(final String executionUri, final ReportRequest request) {
        try {
            String responseBody = webClient.post()
                    .uri(executionUri)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return mapper.readTree(responseBody);
        } catch (Exception e) {
            throw new ExportException("Unable to execute report", e);
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
            UriResponse uriResponse = webClient.post()
                    .uri(EXPORTING_URI)
                    .bodyValue(root)
                    .retrieve()
                    .bodyToMono(UriResponse.class)
                    .block();
            return notNullState(uriResponse, "exported report").getUri();
        } catch (Exception e) {
            throw new ExportException("Unable to export report", e);
        }
    }

    /**
     * Export the given dashboard tab in PDF format to the given output stream
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
            task = webClient.post()
                    .uri(CLIENT_EXPORT_URI.replace("{projectId}", projectId))
                    .bodyValue(export)
                    .retrieve()
                    .bodyToMono(AsyncTask.class)
                    .block();
        } catch (Exception e) {
            throw new ExportException("Unable to export dashboard: " + dashboardUri, e);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(notNullState(task, "export pdf task").getUri(), Void.class) {
            @Override
            public boolean isFinished(ClientResponse response) {
                int code = response.statusCode().value();
                if (code == 200) {
                    return true;
                } else if (code == 202) {
                    return false;
                } else {
                    throw new ExportException("Unable to export dashboard: " + dashboardUri +
                            ", unknown HTTP response code: " + code);
                }
            }

            @Override
            protected void onFinish() {
                byte[] data = webClient.get()
                        .uri(task.getUri())
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block();
                if (data != null) {
                    try {
                        output.write(data);
                    } catch (IOException e) {
                        throw new ExportException("Unable to write dashboard export to stream", e);
                    }
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
     */
    public FutureResult<Void> exportCsv(final Report report, final OutputStream output) {
        notNull(report, "report");
        return exportCsv(report, new ExecuteReport(report), output);
    }

    /**
     * Export the given Report Definition using the raw export (without columns/rows limitations)
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
        final String uri;
        try {
            UriResponse response = webClient.post()
                    .uri(RAW_EXPORT_URI.replace("{projectId}", projectId))
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(UriResponse.class)
                    .block();
            if (response == null || response.getUri() == null) {
                throw new ExportException("Empty response, unable to export: " + obj.getUri());
            }
            uri = response.getUri();
        } catch (Exception e) {
            throw new ExportException("Unable to export: " + obj.getUri(), e);
        }

        return new PollResult<>(this, new SimplePollHandler<Void>(uri, Void.class) {
            @Override
            public boolean isFinished(ClientResponse response) {
                int code = response.statusCode().value();
                if (code == 200) { // OK
                    return true;
                } else if (code == 202) { // ACCEPTED
                    return false;
                } else if (code == 204) { // NO_CONTENT
                    throw new NoDataExportException();
                } else {
                    throw new ExportException("Unable to export: " + uri +
                            ", unknown HTTP response code: " + code);
                }
            }

            @Override
            protected void onFinish() {
                byte[] data = webClient.get()
                        .uri(uri)
                        .retrieve()
                        .bodyToMono(byte[].class)
                        .block();
                if (data != null) {
                    try {
                        output.write(data);
                    } catch (IOException e) {
                        throw new ExportException("Unable to write export to output stream", e);
                    }
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
