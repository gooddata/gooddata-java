/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.report;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.ReportDefinition;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.OutputStream;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

/**
 * Service for report execution and export
 */
public class ReportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    public ReportService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String exportReport(final ReportDefinition reportDefinition, final ReportExportFormat format) {
        notNull(reportDefinition, "reportDefinition");
        notNull(format, "format");
        final JsonNode execResult = executeReport(reportDefinition.getUri());
        return exportReport(execResult, format);
    }

    public void exportReport(final ReportDefinition reportDefinition, final ReportExportFormat format,
                             final OutputStream output) {
        notNull(output, "output");
        final String uri = exportReport(reportDefinition, format);
        try {
            restTemplate.execute(uri, GET, noopRequestCallback, new OutputStreamResponseExtractor(output));
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to export report", e);
        }
    }

    private JsonNode executeReport(final String reportDefinitionUri) {
        notEmpty(reportDefinitionUri, "reportDefinitionUri");
        try {
            final ResponseEntity<String> entity = restTemplate
                    .exchange(ReportRequest.URI, POST, new HttpEntity<>(new ReportRequest(reportDefinitionUri)),
                            String.class);
            return mapper.readTree(entity.getBody());
        } catch (GoodDataException | RestClientException e) {
            throw new GoodDataException("Unable to execute report", e);
        } catch (IOException e) {
            throw new GoodDataException("Unable to read execution result", e);
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
            throw new GoodDataException("Unable to export report", e);
        }
    }
}
