/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
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
     * Get GoodData API root link
     *
     * @return GoodData API root link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getHomeLink() {
        return getLink(LinkCategory.HOME).getUri();
    }

    /**
     * Get temporary token generator link
     *
     * @return temporary token generator link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getTokenLink() {
        return getLink(LinkCategory.TOKEN).getUri();
    }

    /**
     * Get authentication service link
     *
     * @return authentication service link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getLoginLink() {
        return getLink(LinkCategory.LOGIN).getUri();
    }

    /**
     * Get metadata resources link
     *
     * @return metadata resources link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getMetadataLink() {
        return getLink(LinkCategory.METADATA).getUri();
    }

    /**
     * Get report execution resource link
     *
     * @return report execution resource link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getXTabLink() {
        return getLink(LinkCategory.XTAB).getUri();
    }

    /**
     * Get link of resource used to determine valid attribute values in the context of a report
     *
     * @return link of resource used to determine valid attribute values in the context of a report
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getAvailableElementsLink() {
        return getLink(LinkCategory.AVAILABLE_ELEMENTS).getUri();
    }

    /**
     * Get report exporting resource link
     *
     * @return report exporting resource link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getReportExporterLink() {
        return getLink(LinkCategory.REPORT_EXPORTER).getUri();
    }

    /**
     * Get account manipulation resource link
     *
     * @return account manipulation resource link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getAccountLink() {
        return getLink(LinkCategory.ACCOUNT).getUri();
    }

    /**
     * Get user and project management resource link
     *
     * @return user and project management resource link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getProjectsLink() {
        return getLink(LinkCategory.PROJECTS).getUri();
    }

    /**
     * Get miscellaneous tool resource link
     *
     * @return miscellaneous tool resource link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getToolLink() {
        return getLink(LinkCategory.TOOL).getUri();
    }

    /**
     * Get template resource link
     *
     * @return template resource link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getTemplatesLink() {
        return getLink(LinkCategory.TEMPLATES).getUri();
    }

    /**
     * Get release information link
     *
     * @return release information link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getReleaseInfoLink() {
        return getLink(LinkCategory.RELEASE_INFO).getUri();
    }

    /**
     * Get user data staging area link
     *
     * @return user data staging area link
     * @throws GoodDataException in case no link with such category found
     */
    @JsonIgnore
    public String getUserStagingLink() {
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