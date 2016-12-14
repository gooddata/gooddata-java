/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

/**
 * Account setting
 */
@JsonTypeName("accountSetting")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Account {

    public static final String URI = "/gdc/account/profile/{id}";
    public static final String ACCOUNTS_URI = "/gdc/account/domains/{organization_name}/users";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    public static final UriTemplate ACCOUNTS_TEMPLATE = new UriTemplate(ACCOUNTS_URI);

    public static final String LOGIN_URI = "/gdc/account/login/{id}";
    public static final UriTemplate LOGIN_TEMPLATE = new UriTemplate(LOGIN_URI);

    public static final String CURRENT_ID = "current";

    private final String login;
    private final String email;
    private final String password;
    private final String verifyPassword;
    private final String firstName;
    private final String lastName;
    @JsonIgnore
    private final Links links;

    @JsonCreator
    private Account(
            @JsonProperty("login") String login,
            @JsonProperty("email") String email,
            @JsonProperty("password") String password,
            @JsonProperty("verifyPassword") String verifyPassword,
            @JsonProperty("firstName") String firstName,
            @JsonProperty("lastName") String lastName,
            @JsonProperty("links") Links links
    ) {
        this.login = login;
        this.email = email;
        this.password = password;
        this.verifyPassword = verifyPassword;
        this.firstName = firstName;
        this.lastName = lastName;
        this.links = links;
    }

    public Account(String firstName,String lastName, Links links) {
        this(null, null, null, null, firstName, lastName, links);
    }

    /**
     * Account creation constructor
     * @param email email
     * @param firstName first name
     * @param lastName last name
     * @param password password
     */
    public Account(String email, String password, String firstName, String lastName) {
        this(email, email, password, password, firstName, lastName, null);
    }

    public String getLogin() {
        return login;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getVerifyPassword() {
        return verifyPassword;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    @JsonIgnore
    public String getUri() {
        return links.getSelf();
    }

    /**
     * @return projects URI string
     * @deprecated use {@link #getProjectsUri()} instead
     */
    @Deprecated
    @JsonIgnore
    public String getProjectsLink() {
        return getProjectsUri();
    }

    @JsonIgnore
    public String getProjectsUri() {
        return links.getProjects();
    }

    @JsonIgnore
    public String getId() {
        return getId(getUri());
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

    static String getId(String uri) {
        return TEMPLATE.match(uri).get("id");
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.toStringExclude(this, "password", "verifyPassword");
    }
}
