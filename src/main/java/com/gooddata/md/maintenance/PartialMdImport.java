package com.gooddata.md.maintenance;

import static com.gooddata.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

/**
 * Partial metadata import configuration structure.
 * Serialization only.
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

    public PartialMdImport(PartialImportToken partialImportToken) {
        this(partialImportToken, false, false);
    }

    public PartialMdImport(PartialImportToken partialImportToken, boolean overwriteNewer, boolean updateLDMObjects) {
        this(partialImportToken.getToken(), partialImportToken.isExportAttributeProperties(), overwriteNewer, updateLDMObjects);
    }

    /**
     * @param token token identifying metadata partially exported from another project
     * @param overwriteNewer overwrite UDM/ADM objects without checking modification time
     * @param updateLDMObjects overwrite related LDM objects name, description and tags
     * @param importAttributeProperties clone following attribute properties:
     *                                  <ul>
     *                                      <li>for attribute - import drillDownStepAttributeDF setting</li>
     *                                      <li>for attributeDisplayForm - import type setting</li>
     *                                  </ul>
     *                                  it also implies 'updateLDMObjects = true' for all mentioned types;
     *                                  it will not reliably work (can fail) for exports without 'exportAttributeProperties = true'
     */
    public PartialMdImport(String token, boolean importAttributeProperties, boolean overwriteNewer, boolean updateLDMObjects) {
        this.token = notEmpty(token, "token");
        this.importAttributeProperties = importAttributeProperties;
        this.overwriteNewer = overwriteNewer;
        this.updateLDMObjects = updateLDMObjects;
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
