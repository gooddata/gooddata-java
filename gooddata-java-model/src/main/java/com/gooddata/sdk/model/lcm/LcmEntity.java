/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.lcm;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.Map;
import java.util.Objects;

import static com.gooddata.sdk.model.lcm.LcmEntity.LinkCategory.CLIENT;
import static com.gooddata.sdk.model.lcm.LcmEntity.LinkCategory.DATA_PRODUCT;
import static com.gooddata.sdk.model.lcm.LcmEntity.LinkCategory.PROJECT;
import static com.gooddata.sdk.model.lcm.LcmEntity.LinkCategory.SEGMENT;
import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;
import static com.gooddata.sdk.common.util.Validate.notNullState;

/**
 * Single Life Cycle Management entity representing the relation between {@link Project},
 * Client, Segment and DataProduct.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LcmEntity {

    private final String projectId;
    private final String projectTitle;
    private final String clientId;
    private final String segmentId;
    private final String dataProductId;
    private final Map<String, String> links;

    /**
     * Creates new instance of given project id and title
     * @param projectId id of the project
     * @param projectTitle title of the project
     * @param links links
     */
    public LcmEntity(final String projectId, final String projectTitle, final Map<String, String> links) {
        this(projectId, projectTitle, null, null, null, links);
    }

    @JsonCreator
    public LcmEntity(@JsonProperty("projectId") final String projectId,
                     @JsonProperty("projectTitle") final String projectTitle,
                     @JsonProperty("clientId") final String clientId,
                     @JsonProperty("segmentId") final String segmentId,
                     @JsonProperty("dataProductId") final String dataProductId,
                     @JsonProperty("links") final Map<String, String> links) {
        this.projectId = notEmpty(projectId, "projectId");
        this.projectTitle = notNull(projectTitle, "projectTitle");
        this.clientId = clientId;
        this.segmentId = segmentId;
        this.dataProductId = dataProductId;
        this.links = notNull(links, "links");
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public String getDataProductId() {
        return dataProductId;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    @JsonIgnore
    public String getProjectUri() {
        return getLink(PROJECT);
    }

    @JsonIgnore
    public String getClientUri() {
        return getLink(CLIENT);
    }

    @JsonIgnore
    public String getSegmentUri() {
        return getLink(SEGMENT);
    }

    @JsonIgnore
    public String getDataProductUri() {
        return getLink(DATA_PRODUCT);
    }

    private String getLink(final String link) {
        return notNullState(links, "links").get(link);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final LcmEntity lcmEntity = (LcmEntity) o;
        return Objects.equals(projectId, lcmEntity.projectId) &&
                Objects.equals(projectTitle, lcmEntity.projectTitle) &&
                Objects.equals(clientId, lcmEntity.clientId) &&
                Objects.equals(segmentId, lcmEntity.segmentId) &&
                Objects.equals(dataProductId, lcmEntity.dataProductId) &&
                Objects.equals(links, lcmEntity.links);
    }

    @Override
    public int hashCode() {

        return Objects.hash(projectId, projectTitle, clientId, segmentId, dataProductId, links);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    public static class LinkCategory {
        public static final String PROJECT = "project";
        public static final String CLIENT = "client";
        public static final String SEGMENT = "segment";
        public static final String DATA_PRODUCT = "dataProduct";
    }
}
