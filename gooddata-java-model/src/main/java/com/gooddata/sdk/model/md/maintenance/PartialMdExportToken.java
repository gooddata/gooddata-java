/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import static com.gooddata.sdk.common.util.Validate.notEmpty;

/**
 * Partial metadata export token. Serves as configuration structure for import.
 * Serialization only.
 */
@JsonTypeName("partialMDImport")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
public class PartialMdExportToken {

    public static final String URI = "/gdc/md/{projectId}/maintenance/partialmdimport";

    private final String token;
    private boolean overwriteNewer;
    private boolean updateLDMObjects;
    private boolean importAttributeProperties;

    /**
     * Creates new PartialMdExportToken.
     * <br>
     * Sets default values to properties:
     * <ul>
     *     <li>importAttributeProperties - default false</li>
     *     <li>overwriteNewer - default true</li>
     *     <li>updateLDMObjects - default false</li>
     * </ul>
     *
     * @param token token identifying metadata partially exported from another project
     */
    public PartialMdExportToken(String token) {
        this(token, false);
    }

    /**
     * Creates new PartialMdExportToken. For internal purposes only.
     * <br/>
     * Sets default values to properties:
     * <ul>
     *     <li>overwriteNewer - default true</li>
     *     <li>updateLDMObjects - default false</li>
     * </ul>
     *
     * @param token token identifying metadata partially exported from another project
     * @param importAttributeProperties see {@link #setImportAttributeProperties(boolean)}
     */
    public PartialMdExportToken(String token, boolean importAttributeProperties) {
        this.token = notEmpty(token, "token");
        setImportAttributeProperties(importAttributeProperties);
        setOverwriteNewer(true);
        setUpdateLDMObjects(false);
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

    /**
     * Sets the flag {@code overwriteNewer}.
     * If {@code true}, UDM/ADM objects are overwritten without checking modification time.
     *
     * @param overwriteNewer flag value to be set
     */
    public void setOverwriteNewer(boolean overwriteNewer) {
        this.overwriteNewer = overwriteNewer;
    }

    /**
     * Sets the flag {@code updateLDMObjects}.
     * If {@code true}, related LDM objects name, description and tags are overwritten
     *
     * @param updateLDMObjects flag value to be set
     */
    public void setUpdateLDMObjects(boolean updateLDMObjects) {
        this.updateLDMObjects = updateLDMObjects;
    }

    /**
     * Sets the flag {@code importAttributeProperties}.
     * If {@code true}, following attribute properties are cloned:
     * <ul>
     *     <li>for attribute - import drillDownStepAttributeDF setting</li>
     *     <li>for attributeDisplayForm - import type setting</li>
     * </ul>
     * It also implies {@code 'updateLDMObjects = true'} for all mentioned types.<br>
     * It will not reliably work (can fail) for exports without {@code 'exportAttributeProperties = true'}.
     *
     * @param importAttributeProperties flag value to be set
     */
    public void setImportAttributeProperties(boolean importAttributeProperties) {
        this.importAttributeProperties = importAttributeProperties;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
