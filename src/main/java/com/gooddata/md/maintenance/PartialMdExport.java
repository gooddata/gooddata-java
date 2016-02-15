package com.gooddata.md.maintenance;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;

/**
 * Partial metadata export configuration structure
 */
@JsonTypeName("partialMDExport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PartialMdExport {

    public static final String URI = "/gdc/md/{projectId}/maintenance/partialmdexport";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private final List<String> uris;
    private final boolean crossDataCenterExport;
    private final boolean exportAttributeProperties;

    // only to satisfy Jackson NON_DEFAULT serialization, do not use!
    private PartialMdExport() {
        this(Collections.<String>emptyList(), false, false);
    }

    @JsonCreator
    public PartialMdExport(
            @JsonProperty("uris") List<String> uris,
            @JsonProperty("crossDataCenterExport") boolean crossDataCenterExport,
            @JsonProperty("exportAttributeProperties") boolean exportAttributeProperties) {
        this.uris = uris;
        this.crossDataCenterExport = crossDataCenterExport;
        this.exportAttributeProperties = exportAttributeProperties;
    }

    public List<String> getUris() {
        return uris;
    }

    public boolean isCrossDataCenterExport() {
        return crossDataCenterExport;
    }

    public boolean isExportAttributeProperties() {
        return exportAttributeProperties;
    }
}
