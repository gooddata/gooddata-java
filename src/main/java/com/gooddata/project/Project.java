/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.project;

import com.gooddata.md.Meta;
import com.gooddata.util.BooleanIntegerDeserializer;
import com.gooddata.util.BooleanStringDeserializer;
import com.gooddata.util.GDDateTimeDeserializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Project in GoodData platform
 */
@JsonTypeName("project")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Project {

    public static final String PROJECTS_URI = "/gdc/account/profile/{id}/projects";
    public static final String URI = Projects.URI + "/{id}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    private static final Set<String> PREPARING_STATES = new HashSet<>(asList("PREPARING", "PREPARED", "LOADING"));

    @JsonProperty("content")
    private ProjectContent content;

    @JsonProperty("meta")
    private ProjectMeta meta;

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
        return TEMPLATE.match(getUri()).get("id");
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
        return links.getSelf();
    }

    @JsonIgnore
    public String getUsersLink() {
        return links.getUsers();
    }

    @JsonIgnore
    public String getRolesLink() {
        return links.getRoles();
    }

    @JsonIgnore
    public String getGroupsLink() {
        return links.getGroups();
    }

    @JsonIgnore
    public String getInvitationsLink() {
        return links.getInvitations();
    }

    @JsonIgnore
    public String getLdmLink() {
        return links.getLdm();
    }

    @JsonIgnore
    public String getLdmThumbnailLink() {
        return links.getLdmThumbnail();
    }

    @JsonIgnore
    public String getMetadataLink() {
        return links.getMetadata();
    }

    @JsonIgnore
    public String getPublicArtifactsLink() {
        return links.getPublicArtifacts();
    }

    @JsonIgnore
    public String getTemplatesLink() {
        return links.getTemplates();
    }

    @JsonIgnore
    public String getConnectorsLink() {
        return links.getConnectors();
    }

    @JsonIgnore
    public String getDataLoadLink() {
        return links.getDataLoad();
    }

    @JsonIgnore
    public String getSchedulesLink() {
        return links.getSchedules();
    }

    @JsonIgnore
    public String getExecuteLink() {
        return links.getExecute();
    }

    @JsonIgnore
    public String getEventStoresLink() {
        return links.getEventStores();
    }

    @JsonIgnore
    public String getClearCachesLink() {
        return links.getClearCaches();
    }

    @JsonIgnore
    public String getUploadsLink() {
        return links.getUploads();
    }

    @JsonIgnore
    public boolean isPreparing() {
        return PREPARING_STATES.contains(getState());
    }

    @JsonIgnore
    public boolean isEnabled() {
        return "ENABLED".equals(getState());
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    private static class ProjectContent {

        @JsonProperty("authorizationToken")
        private String authorizationToken;

        @JsonProperty("driver")
        private String driver;

        @JsonProperty("guidedNavigation")
        private String guidedNavigation;

        @JsonIgnore
        private String cluster;

        @JsonIgnore
        private String isPublic;

        @JsonIgnore
        private String state;

        @JsonCreator
        public ProjectContent(@JsonProperty("authorizationToken") String authorizationToken,
                              @JsonProperty("driver") String driver,
                              @JsonProperty("cluster") String cluster,
                              @JsonProperty("guidedNavigation") String guidedNavigation,
                              @JsonProperty("isPublic") String isPublic,
                              @JsonProperty("state") String state) {
            this.authorizationToken = authorizationToken;
            this.guidedNavigation = guidedNavigation;
            this.driver = driver;
            this.cluster = cluster;
            this.isPublic = isPublic;
            this.state = state;
        }

        public ProjectContent(final String authorizationToken) {
            this.authorizationToken = authorizationToken;
            guidedNavigation = "1";
            driver = "Pg";
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
        private ProjectMeta(@JsonProperty("author") String author, @JsonProperty("contributor") String contributor,
                            @JsonProperty("created") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime created,
                            @JsonProperty("updated") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime updated,
                            @JsonProperty("summary") String summary, @JsonProperty("title") String title,
                            @JsonProperty("category") String category, @JsonProperty("tags") String tags,
                            @JsonProperty("uri") String uri,
                            @JsonProperty("deprecated") @JsonDeserialize(using = BooleanStringDeserializer.class) boolean deprecated,
                            @JsonProperty("identifier") String identifier,
                            @JsonProperty("locked") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean locked,
                            @JsonProperty("unlisted") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean unlisted) {
            super(author, contributor, created, updated, summary, title, category, tags, uri, deprecated, identifier,
                    locked, unlisted);
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
    }
}
