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
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.gooddata.Validate.notEmpty;
import static com.gooddata.Validate.notNull;
import static org.springframework.http.HttpMethod.POST;

/**
 * TODO
 */
public class ReportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    public ReportService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String exportReport(final ReportDefinition reportDefinition, final String format) {
        notNull(reportDefinition, "reportDefinition");
        notEmpty(format, "format");
        final ExecResult execResult = executeReport(reportDefinition.getMeta().getUri());
        return exportReport(execResult, format);
    }

    public ExecResult executeReport(final String reportDefinitionUri) {
        notEmpty(reportDefinitionUri, "reportDefinitionUri");
        final ResponseEntity<String> entity = restTemplate
                .exchange(ReportRequest.URI, POST, new HttpEntity<>(new ReportRequest(reportDefinitionUri)),
                        String.class);
        try {
            return new ExecResult(mapper.readTree(entity.getBody()));
        } catch (IOException e) {
            throw new GoodDataException("Unable to read execution result", e);
        }
    }

    public String exportReport(final ExecResult execResult, final String format) {
        notNull(execResult, "execResult");
        notEmpty(format, "format");
        final ObjectNode root = mapper.createObjectNode();
        final ObjectNode child = mapper.createObjectNode();

        child.put("result", execResult.getJsonNode());
        child.put("format", format);
        root.put("result_req", child);

        return restTemplate.postForObject(EXPORTING_URI, root, UriResponse.class).getUri();
    }
}
