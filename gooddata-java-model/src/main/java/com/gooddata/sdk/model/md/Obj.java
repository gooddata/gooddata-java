/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.md;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.md.visualization.VisualizationClass;
import com.gooddata.sdk.model.md.visualization.VisualizationObject;

/**
 * First class metadata object - only dto objects, which have URI pointing to themselves should implement this.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.WRAPPER_OBJECT)
@JsonSubTypes({
        @JsonSubTypes.Type(Attribute.class),
        @JsonSubTypes.Type(AttributeDisplayForm.class),
        @JsonSubTypes.Type(Column.class),
        @JsonSubTypes.Type(DataLoadingColumn.class),
        @JsonSubTypes.Type(Dataset.class),
        @JsonSubTypes.Type(Dimension.class),
        @JsonSubTypes.Type(Fact.class),
        @JsonSubTypes.Type(Metric.class),
        @JsonSubTypes.Type(ProjectDashboard.class),
        @JsonSubTypes.Type(Report.class),
        @JsonSubTypes.Type(ReportDefinition.class),
        @JsonSubTypes.Type(ScheduledMail.class),
        @JsonSubTypes.Type(Table.class),
        @JsonSubTypes.Type(TableDataLoad.class),
        @JsonSubTypes.Type(VisualizationObject.class),
        @JsonSubTypes.Type(VisualizationClass.class),
})
public interface Obj {

    String URI = "/gdc/md/{projectId}/obj";
    String CREATE_URI = URI + "?createAndGet=true";
    String CREATE_WITH_ID_URI = CREATE_URI + "&setIdentifier=true";
    String OBJ_URI = URI + "/{objId}";

    String getUri();
}
