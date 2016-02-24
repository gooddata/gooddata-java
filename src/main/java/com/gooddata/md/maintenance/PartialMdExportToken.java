package com.gooddata.md.maintenance;

import static com.gooddata.util.Validate.notEmpty;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

/**
 * Partial metadata export token. Serves as configuration structure for import.
 * Serialization only.
 */
@JsonTypeName("partialMDImport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class PartialMdExportToken {

    public static final String URI = "/gdc/md/{projectId}/maintenance/partialmdimport";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private final String token;
    private final boolean overwriteNewer;
    private final boolean updateLDMObjects;
    private final boolean importAttributeProperties;

    /**
     * Creates new PartialMdExportToken.
     *
     * Sets default value (false) to properties:
     * <ul>
     *     <li>importAttributeProperties</li>
     *     <li>overwriteNewer</li>
     *     <li>updateLDMObjects</li>
     * </ul>
     *
     * @param token token identifying metadata partially exported from another project
     */
    public PartialMdExportToken(String token) {
        this(token, false, false, false);
    }

    /**
     * Creates new PartialMdExportToken.
     *
     * Sets default value (false) to properties:
     * <ul>
     *     <li>overwriteNewer</li>
     *     <li>updateLDMObjects</li>
     * </ul>
     *
     * @param token token identifying metadata partially exported from another project
     * @param importAttributeProperties clone following attribute properties:
     *                                  <ul>
     *                                      <li>for attribute - import drillDownStepAttributeDF setting</li>
     *                                      <li>for attributeDisplayForm - import type setting</li>
     *                                  </ul>
     *                                  it also implies 'updateLDMObjects = true' for all mentioned types;
     *                                  it will not reliably work (can fail) for exports without 'exportAttributeProperties = true'
     */
    public PartialMdExportToken(String token, boolean importAttributeProperties) {
        this(token, importAttributeProperties, false, false);
    }

    /**
     * Creates new PartialMdExportToken.
     *
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
    public PartialMdExportToken(String token, boolean importAttributeProperties, boolean overwriteNewer, boolean updateLDMObjects) {
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
