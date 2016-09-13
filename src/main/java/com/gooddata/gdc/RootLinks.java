/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gooddata.GoodDataException;

import java.util.List;

/**
 * GoodData API root links (aka "about" or "home").
 * Deserialization only.
 */
public class RootLinks extends AboutLinks {

    /**
     * URI of GoodData API root
     */
    public static final String URI = "/gdc";

    @JsonCreator
    public RootLinks(@JsonProperty("links") List<Link> links) {
        super(null, null, null, links);
    }

    /**
     * Get GoodData API root URI string
     *
     * @return GoodData API root URI string
     * @deprecated use {@link #getHomeUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getHomeLink() {
        return getHomeUri();
    }

    /**
     * Get GoodData API root URI string
     *
     * @return GoodData API root URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getHomeUri() {
        return getLink(LinkCategory.HOME).getUri();
    }

    /**
     * Get temporary token generator URI string
     *
     * @return temporary token generator URI string
     * @deprecated use {@link #getTokenUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getTokenLink() {
        return getTokenUri();
    }

    /**
     * Get temporary token generator URI string
     *
     * @return temporary token generator URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getTokenUri() {
        return getLink(LinkCategory.TOKEN).getUri();
    }

    /**
     * Get authentication service URI string
     *
     * @return authentication service URI string
     * @deprecated use {@link #getLoginUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getLoginLink() {
        return getLoginUri();
    }

    /**
     * Get authentication service URI string
     *
     * @return authentication service URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getLoginUri() {
        return getLink(LinkCategory.LOGIN).getUri();
    }

    /**
     * Get metadata resources URI string
     *
     * @return metadata resources URI string
     * @deprecated use {@link #getMetadataUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getMetadataLink() {
        return getMetadataUri();
    }

    /**
     * Get metadata resources URI string
     *
     * @return metadata resources URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getMetadataUri() {
        return getLink(LinkCategory.METADATA).getUri();
    }

    /**
     * Get report execution resource URI string
     *
     * @return report execution resource URI string
     * @deprecated use {@link #getXTabUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getXTabLink() {
        return getXTabUri();
    }

    /**
     * Get report execution resource URI string
     *
     * @return report execution resource URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getXTabUri() {
        return getLink(LinkCategory.XTAB).getUri();
    }

    /**
     * Get URI string of resource used to determine valid attribute values in the context of a report
     *
     * @return URI string of resource used to determine valid attribute values in the context of a report
     * @deprecated use {@link #getAvailableElementsUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getAvailableElementsLink() {
        return getAvailableElementsUri();
    }

    /**
     * Get URI string of resource used to determine valid attribute values in the context of a report
     *
     * @return URI string of resource used to determine valid attribute values in the context of a report
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getAvailableElementsUri() {
        return getLink(LinkCategory.AVAILABLE_ELEMENTS).getUri();
    }

    /**
     * Get report exporting resource URI string
     *
     * @return report exporting resource URI string
     * @deprecated use {@link #getReportExporterUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getReportExporterLink() {
        return getReportExporterUri();
    }

    /**
     * Get report exporting resource URI string
     *
     * @return report exporting resource URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getReportExporterUri() {
        return getLink(LinkCategory.REPORT_EXPORTER).getUri();
    }

    /**
     * Get account manipulation resource URI string
     *
     * @return account manipulation resource URI string
     * @deprecated use {@link #getAccountUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getAccountLink() {
        return getAccountUri();
    }

    /**
     * Get account manipulation resource URI string
     *
     * @return account manipulation resource URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getAccountUri() {
        return getLink(LinkCategory.ACCOUNT).getUri();
    }

    /**
     * Get user and project management resource URI string
     *
     * @return user and project management resource URI string
     * @deprecated use {@link #getProjectsUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getProjectsLink() {
        return getProjectsUri();
    }

    /**
     * Get user and project management resource URI string
     *
     * @return user and project management resource URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getProjectsUri() {
        return getLink(LinkCategory.PROJECTS).getUri();
    }

    /**
     * Get miscellaneous tool resource URI string
     *
     * @return miscellaneous tool resource URI string
     * @deprecated use {@link #getToolUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getToolLink() {
        return getToolUri();
    }

    /**
     * Get miscellaneous tool resource URI string
     *
     * @return miscellaneous tool resource URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getToolUri() {
        return getLink(LinkCategory.TOOL).getUri();
    }

    /**
     * Get template resource URI string
     *
     * @return template resource URI string
     * @deprecated use {@link #getTemplatesUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getTemplatesLink() {
        return getTemplatesUri();
    }

    /**
     * Get template resource URI string
     *
     * @return template resource URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getTemplatesUri() {
        return getLink(LinkCategory.TEMPLATES).getUri();
    }

    /**
     * Get release information URI string
     *
     * @return release information URI string
     * @deprecated use {@link #getReleaseInfoUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getReleaseInfoLink() {
        return getReleaseInfoUri();
    }

    /**
     * Get release information URI string
     *
     * @return release information URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getReleaseInfoUri() {
        return getLink(LinkCategory.RELEASE_INFO).getUri();
    }

    /**
     * Get user data staging area URI string
     *
     * @return user data staging area URI string
     * @deprecated use {@link #getUserStagingUri()} instead
     * @throws GoodDataException in case no link with such category found
     */
    @Deprecated
    @JsonIgnore
    public String getUserStagingLink() {
        return getUserStagingUri();
    }

    /**
     * Get user data staging area URI string
     *
     * @return user data staging area URI string
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getUserStagingUri() {
        return getLink(LinkCategory.USER_STAGING).getUri();
    }

    /**
     * Get link by category
     *
     * @param category requested link category
     * @return link by given category
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    private Link getLink(LinkCategory category) {
        for (Link link : getLinks()) {
            if (category.value.equals(link.getCategory())) {
                return link;
            }
        }
        throw new GoodDataException("No link with such category found");
    }

    /**
     * GoodData API root link category enum
     */
    private enum LinkCategory {
        /**
         * GoodData API root
         */
        HOME("home"),
        /**
         * Temporary token generator
         */
        TOKEN("token"),
        /**
         * Authentication service
         */
        LOGIN("login"),
        /**
         * Metadata resources
         */
        METADATA("md"),
        /**
         * Report execution resource
         */
        XTAB("xtab"),
        /**
         * Resource used to determine valid attribute values in the context of a report
         */
        AVAILABLE_ELEMENTS("availablelements"),
        /**
         * Report exporting resource
         */
        REPORT_EXPORTER("report-exporter"),
        /**
         * Resource for logged in account manipulation
         */
        ACCOUNT("account"),
        /**
         * Resource for user and project management
         */
        PROJECTS("projects"),
        /**
         * Miscellaneous resources
         */
        TOOL("tool"),
        /**
         * Template resource - for internal use only
         */
        TEMPLATES("templates"),
        /**
         * Release information
         */
        RELEASE_INFO("releaseInfo"),
        /**
         * User data staging area
         */
        USER_STAGING("uploads");

        private final String value;

        LinkCategory(final String value) {
            this.value = value;
        }
    }

}