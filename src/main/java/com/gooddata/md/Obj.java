/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.md;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.gooddata.md.report.Report;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.md.visualization.VisualizationClass;
import com.gooddata.md.visualization.VisualizationObject;
import org.springframework.web.util.UriTemplate;

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
    UriTemplate OBJ_TEMPLATE = new UriTemplate(OBJ_URI);

    String getUri();
}
