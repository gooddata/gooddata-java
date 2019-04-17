/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.sdk.model.md.Meta;
import com.gooddata.sdk.model.util.UriHelper;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.GDDateTimeDeserializer;
import com.gooddata.util.GoodDataToStringBuilder;
import org.joda.time.DateTime;

import java.util.HashSet;
import java.util.Set;

import static com.gooddata.util.Validate.*;
import static java.util.Arrays.asList;

/**
 * Project in GoodData platform
 */
@JsonTypeName("project")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

    public static final String URI = Projects.URI + "/{id}";
    private static final Set<String> PREPARING_STATES = new HashSet<>(asList("PREPARING", "PREPARED", "LOADING"));

    @JsonProperty("content")
    private final ProjectContent content;

    @JsonProperty("meta")
    private final ProjectMeta meta;

    @JsonIgnore
    private Links links;

    public Project(String title, String authorizationToken) {
        content = new ProjectContent(authorizationToken);
        meta = new ProjectMeta(title);
    }

    public Project(String title, String summary, String authorizationToken) {
        content = new ProjectContent(authorizationToken);
        meta = new ProjectMeta(title, summary);
    }

    @JsonCreator
    private Project(@JsonProperty("content") ProjectContent content, @JsonProperty("meta") ProjectMeta meta,
                    @JsonProperty("links") Links links) {
        this.content = content;
        this.meta = meta;
        this.links = links;
    }

    public void setProjectTemplate(String uri) {
        meta.setProjectTemplate(uri);
    }

    @JsonIgnore
    public String getId() {
        return UriHelper.getLastUriPart(getUri());
    }

    @JsonIgnore
    public String getState() {
        return content.getState();
    }

    @JsonIgnore
    public String getAuthorizationToken() {
        return content.getAuthorizationToken();
    }

    @JsonIgnore
    public String getDriver() {
        return content.getDriver();
    }

    @JsonIgnore
    public String getGuidedNavigation() {
        return content.getGuidedNavigation();
    }

    @JsonIgnore
    public String getCluster() {
        return content.getCluster();
    }

    @JsonIgnore
    public Boolean isPublic() {
        return "1".equals(content.getIsPublic());
    }

    @JsonIgnore
    public String getTitle() {
        return meta.getTitle();
    }

    @JsonIgnore
    public String getSummary() {
        return meta.getSummary();
    }

    @JsonIgnore
    public String getAuthor() {
        return meta.getAuthor();
    }

    @JsonIgnore
    public String getContributor() {
        return meta.getContributor();
    }

    @JsonIgnore
    public DateTime getCreated() {
        return meta.getCreated();
    }

    @JsonIgnore
    public DateTime getUpdated() {
        return meta.getUpdated();
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").getSelf();
    }

    @JsonIgnore
    public String getUsersUri() {
        return notNullState(links, "links").getUsers();
    }

    @JsonIgnore
    public String getRolesUri() {
        return notNullState(links, "links").getRoles();
    }

    @JsonIgnore
    public String getGroupsUri() {
        return notNullState(links, "links").getGroups();
    }

    @JsonIgnore
    public String getInvitationsUri() {
        return notNullState(links, "links").getInvitations();
    }

    @JsonIgnore
    public String getLdmUri() {
        return links.getLdm();
    }

    @JsonIgnore
    public String getLdmThumbnailUri() {
        return notNullState(links, "links").getLdmThumbnail();
    }

    @JsonIgnore
    public String getMetadataUri() {
        return notNullState(links, "links").getMetadata();
    }

    @JsonIgnore
    public String getPublicArtifactsUri() {
        return notNullState(links, "links").getPublicArtifacts();
    }

    @JsonIgnore
    public String getTemplatesUri() {
        return notNullState(links, "links").getTemplates();
    }

    @JsonIgnore
    public String getConnectorsUri() {
        return notNullState(links, "links").getConnectors();
    }

    @JsonIgnore
    public String getDataLoadUri() {
        return notNullState(links, "links").getDataLoad();
    }

    @JsonIgnore
    public String getSchedulesUri() {
        return notNullState(links, "links").getSchedules();
    }

    @JsonIgnore
    public String getExecuteUri() {
        return notNullState(links, "links").getExecute();
    }

    @JsonIgnore
    public String getEventStoresUri() {
        return notNullState(links, "links").getEventStores();
    }

    @JsonIgnore
    public String getClearCachesUri() {
        return notNullState(links, "links").getClearCaches();
    }

    @JsonIgnore
    public String getUploadsUri() {
        return notNullState(links, "links").getUploads();
    }

    @JsonIgnore
    public boolean isPreparing() {
        return PREPARING_STATES.contains(getState());
    }

    @JsonIgnore
    public boolean isEnabled() {
        return "ENABLED".equals(getState());
    }

    public void setDriver(String driver) {
        notEmpty(driver, "driver");
        content.setDriver(driver);
    }

    public void setDriver(ProjectDriver driver) {
        notNull(driver, "driver");
        setDriver(driver.getValue());
    }

    @JsonIgnore
    public String getEnvironment() {
        return content.getEnvironment();
    }

    @JsonIgnore
    public void setEnvironment(final String environment) {
        content.setEnvironment(environment);
    }

    public void setEnvironment(final ProjectEnvironment environment) {
        notNull(environment, "environment");
        setEnvironment(environment.name());
    }

    public void setEnvironment(final Environment environment) {
        notNull(environment, "environment");
        setEnvironment(environment.name());
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class ProjectContent {

        @JsonProperty("authorizationToken")
        private final String authorizationToken;

        @JsonProperty("driver")
        private String driver;

        @JsonProperty("guidedNavigation")
        private final String guidedNavigation;

        @JsonIgnore
        private String cluster;

        @JsonIgnore
        private String isPublic;

        @JsonIgnore
        private String state;

        @JsonProperty
        private String environment;

        public ProjectContent(final String authorizationToken) {
            this.authorizationToken = authorizationToken;
            guidedNavigation = "1";
            driver = ProjectDriver.POSTGRES.getValue();
        }

        @JsonCreator
        public ProjectContent(@JsonProperty("authorizationToken") String authorizationToken,
                              @JsonProperty("driver") String driver,
                              @JsonProperty("cluster") String cluster,
                              @JsonProperty("guidedNavigation") String guidedNavigation,
                              @JsonProperty("isPublic") String isPublic,
                              @JsonProperty("environment") String environment,
                              @JsonProperty("state") String state) {
            this.authorizationToken = authorizationToken;
            this.guidedNavigation = guidedNavigation;
            this.driver = driver;
            this.cluster = cluster;
            this.isPublic = isPublic;
            this.state = state;
            this.environment = environment;
        }

        public String getState() {
            return state;
        }

        public String getAuthorizationToken() {
            return authorizationToken;
        }

        public String getDriver() {
            return driver;
        }

        public String getGuidedNavigation() {
            return guidedNavigation;
        }

        public String getCluster() {
            return cluster;
        }

        public String getIsPublic() {
            return isPublic;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getEnvironment() {
            return environment;
        }

        public void setEnvironment(final String environment) {
            this.environment = environment;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Links {
        private final String self;
        private final String users;
        private final String roles;
        private final String groups;
        private final String invitations;
        private final String ldm;
        private final String ldmThumbnail;
        private final String metadata;
        private final String publicArtifacts;
        private final String templates;
        private final String connectors;
        private final String dataLoad;
        private final String schedules;
        private final String execute;
        private final String eventStores;
        private final String clearCaches;
        private final String uploads;

        @JsonCreator
        public Links(@JsonProperty("self") String self,
                     @JsonProperty("users") String users,
                     @JsonProperty("roles") String roles,
                     @JsonProperty("groups") String groups,
                     @JsonProperty("invitations") String invitations,
                     @JsonProperty("ldm") String ldm,
                     @JsonProperty("ldm_thumbnail") String ldmThumbnail,
                     @JsonProperty("metadata") String metadata,
                     @JsonProperty("publicartifacts") String publicArtifacts,
                     @JsonProperty("templates") String templates,
                     @JsonProperty("connectors") String connectors,
                     @JsonProperty("dataload") String dataLoad,
                     @JsonProperty("schedules") String schedules,
                     @JsonProperty("execute") String execute,
                     @JsonProperty("eventstores") String eventStores,
                     @JsonProperty("clearCaches") String clearCaches,
                     @JsonProperty("uploads") String uploads) {
            this.self = self;
            this.users = users;
            this.roles = roles;
            this.groups = groups;
            this.invitations = invitations;
            this.ldm = ldm;
            this.ldmThumbnail = ldmThumbnail;
            this.metadata = metadata;
            this.publicArtifacts = publicArtifacts;
            this.templates = templates;
            this.connectors = connectors;
            this.dataLoad = dataLoad;
            this.schedules = schedules;
            this.execute = execute;
            this.eventStores = eventStores;
            this.clearCaches = clearCaches;
            this.uploads = uploads;
        }

        public String getSelf() {
            return self;
        }

        public String getUsers() {
            return users;
        }

        public String getRoles() {
            return roles;
        }

        public String getGroups() {
            return groups;
        }

        public String getInvitations() {
            return invitations;
        }

        public String getLdm() {
            return ldm;
        }

        public String getLdmThumbnail() {
            return ldmThumbnail;
        }

        public String getMetadata() {
            return metadata;
        }

        public String getPublicArtifacts() {
            return publicArtifacts;
        }

        public String getTemplates() {
            return templates;
        }

        public String getConnectors() {
            return connectors;
        }

        public String getDataLoad() {
            return dataLoad;
        }

        public String getSchedules() {
            return schedules;
        }

        public String getExecute() {
            return execute;
        }

        public String getEventStores() {
            return eventStores;
        }

        public String getClearCaches() {
            return clearCaches;
        }

        public String getUploads() {
            return uploads;
        }
    }

    private static class ProjectMeta extends Meta {

        private String projectTemplate;

        @JsonCreator
        private ProjectMeta(@JsonProperty("author") String author,
                            @JsonProperty("contributor") String contributor,
                            @JsonProperty("created") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime created,
                            @JsonProperty("updated") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime updated,
                            @JsonProperty("summary") String summary,
                            @JsonProperty("title") String title,
                            @JsonProperty("category") String category,
                            @JsonProperty("tags") Set<String> tags,
                            @JsonProperty("uri") String uri,
                            @JsonProperty("identifier") String identifier,
                            @JsonProperty("deprecated") @JsonDeserialize(using = BooleanDeserializer.class) Boolean deprecated,
                            @JsonProperty("isProduction") @JsonDeserialize(using = BooleanDeserializer.class) Boolean production,
                            @JsonProperty("locked") @JsonDeserialize(using = BooleanDeserializer.class) Boolean locked,
                            @JsonProperty("unlisted") @JsonDeserialize(using = BooleanDeserializer.class) Boolean unlisted,
                            @JsonProperty("sharedWithSomeone") @JsonDeserialize(using = BooleanDeserializer.class) Boolean sharedWithSomeone,
                            @JsonProperty("flags") Set<String> flags) {
            super(author, contributor, created, updated, summary, title, category, tags, uri, identifier,
                    deprecated, production, locked, unlisted, sharedWithSomeone, flags);

        }

        private ProjectMeta(String title) {
            super(title);
        }

        private ProjectMeta(String title, String summary) {
            super(title, summary);
        }

        public String getProjectTemplate() {
            return projectTemplate;
        }

        public void setProjectTemplate(String projectTemplate) {
            this.projectTemplate = projectTemplate;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
