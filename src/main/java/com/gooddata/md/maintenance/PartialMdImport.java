package com.gooddata.md.maintenance;

import static com.gooddata.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

/**
 * Partial metadata import configuration structure
 */
@JsonTypeName("partialMDImport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class PartialMdImport {

    public static final String URI = "/gdc/md/{projectId}/maintenance/partialmdimport";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private final String token;
    private final boolean overwriteNewer;
    private final boolean updateLDMObjects;
    private final boolean importAttributeProperties;

    @JsonCreator
    public PartialMdImport(
            @JsonProperty("token") String token,
            @JsonProperty("overwriteNewer") boolean overwriteNewer,
            @JsonProperty("updateLDMObjects") boolean updateLDMObjects,
            @JsonProperty("importAttributeProperties") boolean importAttributeProperties) {
        this.token = notEmpty(token, "token");
        this.overwriteNewer = overwriteNewer;
        this.updateLDMObjects = updateLDMObjects;
        this.importAttributeProperties = importAttributeProperties;
    }

    public String getToken() {
        return token;
    }

    public boolean isOverwriteNewer() {
        return overwriteNewer;
    }

    public boolean isUpdateLDMObjects() {
        return updateLDMObjects;
    }

    public boolean isImportAttributeProperties() {
        return importAttributeProperties;
    }
}
