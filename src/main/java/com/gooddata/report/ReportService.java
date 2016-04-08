/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;

import com.gooddata.AbstractService;
import com.gooddata.FutureResult;
import com.gooddata.PollResult;
import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.SimplePollHandler;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;

import static com.gooddata.util.Validate.notNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Service for report export
 */
public class ReportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    public ReportService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    /**
     * Export the given report definition in the given format to the given output strream
     * @param reportDefinition report definition
     * @param format export format
     * @param output target
     * @return polling result
     * @throws NoDataReportException in case report contains no data
     * @throws ReportException on error
     */
    public FutureResult<Void> exportReport(final ReportDefinition reportDefinition, final ReportExportFormat format,
                                           final OutputStream output) {
        notNull(reportDefinition, "reportDefinition");
        final ReportRequest request = new ExecuteDefinition(reportDefinition.getUri());
        return exportReport(request, format, output);
    }

    /**
     * Export the given report in the given format to the given output strream
     * @param report report
     * @param format export format
     * @param output target
     * @return polling result
     * @throws NoDataReportException in case report contains no data
     * @throws ReportException on error
     */
    public FutureResult<Void> exportReport(final Report report, final ReportExportFormat format,
                                           final OutputStream output) {
        notNull(report, "report");
        final ReportRequest request = new ExecuteReport(report.getUri());
        return exportReport(request, format, output);
    }

    private FutureResult<Void> exportReport(final ReportRequest request, final ReportExportFormat format, final OutputStream output) {
        notNull(output, "output");
        notNull(format, "format");
        final JsonNode execResult = executeReport(request);
        final String uri = exportReport(execResult, format);
        return new PollResult<>(this, new SimplePollHandler<Void>(uri, Void.class) {
            @Override
            public boolean isFinished(ClientHttpResponse response) throws IOException {
                switch (response.getStatusCode()) {
                    case OK: return true;
                    case ACCEPTED: return false;
                    case NO_CONTENT: throw new NoDataReportException();
                    default: throw new ReportException("Unable to export report, unknown HTTP response code: " + response.getStatusCode());
                }
            }

            @Override
            public void handlePollException(final GoodDataRestException e) {
                throw new ReportException("Unable to export report", e);
            }

            @Override
            protected void onFinish() {
                try {
                    restTemplate.execute(uri, GET, null, new OutputStreamResponseExtractor(output));
                } catch (GoodDataException | RestClientException e) {
                    throw new ReportException("Unable to export report", e);
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
            throw new ReportException("Unable to execute report", e);
        } catch (IOException e) {
            throw new ReportException("Unable to read execution result", e);
        }
    }

    private String exportReport(final JsonNode execResult, final ReportExportFormat format) {
        notNull(execResult, "execResult");
        notNull(format, "format");
        final ObjectNode root = mapper.createObjectNode();
        final ObjectNode child = mapper.createObjectNode();

        child.put("result", execResult);
        child.put("format", format.getValue());
        root.put("result_req", child);

        try {
            return restTemplate.postForObject(EXPORTING_URI, root, UriResponse.class).getUri();
        } catch (GoodDataException | RestClientException e) {
            throw new ReportException("Unable to export report", e);
        }
    }
}
