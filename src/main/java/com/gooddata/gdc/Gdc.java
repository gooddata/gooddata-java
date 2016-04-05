/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.List;

/**
 * GoodData API root response (aka "about" or "home")
 */
@JsonTypeName("about")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Gdc {

    /**
     * URI of GoodData API root
     */
    public static final String URI = "/gdc";

    private final List<Link> links;

    @JsonCreator
    public Gdc(@JsonProperty("links") List<Link> links) {
        this.links = links;
    }

    /**
     * Get GoodData API root URI string
     *
     * @return GoodData API root URI string
     * @deprecated use {@link #getHomeUri()} instead
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
     */
    @JsonIgnore
    private Link getLink(LinkCategory category) {
        for (Link link : links) {
            if (category.value.equals(link.getCategory())) {
                return link;
            }
        }
        return null;
    }

    /**
     * GoodData API root link
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Link {
        private final String category;
        private final String uri;
        private final String summary;
        private final String title;

        @JsonCreator
        public Link(@JsonProperty("category") String category, @JsonProperty("link") String uri,
                    @JsonProperty("summary") String summary, @JsonProperty("title") String title) {
            this.category = category;
            this.uri = uri;
            this.summary = summary;
            this.title = title;
        }

        /**
         * Get link category
         *
         * @return link category
         */
        public String getCategory() {
            return category;
        }

        /**
         * Get link URI
         *
         * @return link URI
         */
        public String getUri() {
            return uri;
        }

        /**
         * Get link summary
         *
         * @return link summary
         */
        public String getSummary() {
            return summary;
        }

        /**
         * Get link title
         *
         * @return link title
         */
        public String getTitle() {
            return title;
        }
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