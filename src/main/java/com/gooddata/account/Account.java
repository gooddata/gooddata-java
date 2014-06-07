/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.account;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriTemplate;

/**
 * Account setting
 */
@JsonTypeName("accountSetting")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Account {

    public static final String URI = "/gdc/account/profile/{id}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    public static final String LOGIN_URI = "/gdc/account/login/{id}";
    public static final UriTemplate LOGIN_TEMPLATE = new UriTemplate(LOGIN_URI);

    public static final String CURRENT_ID = "current";


    private final String firstName;
    private final String lastName;
    @JsonIgnore
    private final Links links;

    @JsonCreator
    public Account(@JsonProperty("firstName") String firstName,
                   @JsonProperty("lastName") String lastName,
                   @JsonProperty("links") Links links) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.links = links;
    }

    /* Just for serialization test */
    Account(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        links = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonIgnore
    public String getSelfLink() {
        return links.getSelf();
    }

    @JsonIgnore
    public String getProjectsLink() {
        return links.getProjects();
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getSelfLink()).get("id");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Links {
        private final String self;
        private final String projects;

        @JsonCreator
        public Links(@JsonProperty("self") String self, @JsonProperty("projects") String projects) {
            this.self = self;
            this.projects = projects;
        }

        public String getSelf() {
            return self;
        }

        public String getProjects() {
            return projects;
        }
    }
}
