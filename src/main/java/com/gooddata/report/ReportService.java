package com.gooddata.report;

import com.gooddata.AbstractService;
import com.gooddata.GoodDataException;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.ReportDefinition;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.springframework.http.HttpMethod.POST;

/**
 * TODO
 */
public class ReportService extends AbstractService {

    public static final String EXPORTING_URI = "/gdc/exporter/executor";

    private ObjectMapper mapper = new ObjectMapper();

    public ReportService(final RestTemplate restTemplate) {
        super(restTemplate);
    }

    public String exportReport(final ReportDefinition reportDefinition, final String format) {
        final JsonNode execResult = executeReport(reportDefinition.getMeta().getUri());
        return exportReport(execResult, format);
    }

    public JsonNode executeReport(final String reportDefinitionUri) {
        final ResponseEntity<String> entity = restTemplate
                .exchange(ReportRequest.URI, POST, new HttpEntity<>(new ReportRequest(reportDefinitionUri)),
                        String.class);
        try {
            return mapper.readTree(entity.getBody());
        } catch (IOException e) {
            throw new GoodDataException("Unable to read execution result", e);
        }
    }

    public String exportReport(final JsonNode execResult, final String format) {
        final ObjectNode root = mapper.createObjectNode();
        final ObjectNode child = mapper.createObjectNode();

        child.put("result", execResult);
        child.put("format", format);
        root.put("result_req", child);

        return restTemplate.postForObject(EXPORTING_URI, root, UriResponse.class).getUri();
    }
}
