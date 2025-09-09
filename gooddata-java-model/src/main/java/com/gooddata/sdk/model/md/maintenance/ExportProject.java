/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md.maintenance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.util.BooleanStringSerializer;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Collection;
import java.util.HashSet;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static java.util.Arrays.asList;

/**
 * Complete project export configuration structure.
 * Serialization only.
 */
@JsonTypeName("exportProject")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportProject {

    public static final String URI = "/gdc/md/{projectId}/maintenance/export";

    private final boolean exportUsers;
    private final boolean exportData;
    private final boolean excludeSchedules;
    private final boolean crossDataCenterExport;
    private final String authorizedUsers;

    /**
     * Creates new ExportProject with default settings.
     * Sets exportUsers=true exportData=true, excludeSchedules=false, crossDataCenterExport=false and empty authorizedUsers.
     */
    public ExportProject() {
        this(true, true, false, false, null);
    }

    /**
     * Creates new ExportProject.
     *
     * @param exportUsers whether to add necessary data to be able to clone attribute properties
     * @param exportData whether to add necessary data to be able to clone attribute properties
     * @param excludeSchedules whether to add necessary data to be able to clone attribute properties
     * @param crossDataCenterExport whether export should be usable in any Data Center
     * @param authorizedUsers comma-separated list of email addresses of users authorized to import the project, surround email addresses with double quotes
     */
    public ExportProject(final boolean exportUsers, final boolean exportData, final boolean excludeSchedules, final boolean crossDataCenterExport, final String authorizedUsers) {
        this.exportUsers = exportUsers;
        this.exportData = exportData;
        this.excludeSchedules = excludeSchedules;
        this.crossDataCenterExport = crossDataCenterExport;
        this.authorizedUsers = authorizedUsers;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isExportUsers() {
        return exportUsers;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isExportData() {
        return exportData;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isExcludeSchedules() {
        return excludeSchedules;
    }

    @JsonSerialize(using = BooleanStringSerializer.class)
    public boolean isCrossDataCenterExport() {
        return crossDataCenterExport;
    }

    public String getAuthorizedUsers() {
        return authorizedUsers;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}

