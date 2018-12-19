/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * User in project
 * @see Account
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("user")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {

    public static final String URI = "/gdc/projects/{projectId}/users/{userId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    @JsonProperty
    private UserContent content;

    private Links links;

    @JsonCreator
    User(@JsonProperty("content") final UserContent content,
         @JsonProperty("links") final Links links) {
        this.content = content;
        this.links = links;
    }

    public User(final Account account,
                final Role... userRoles) {
        final List<String> userRoleUris = Arrays.asList(userRoles).stream().map(e -> e.getUri()).collect(Collectors.toList());

        links = new Links(account.getUri());
        content = new UserContent("ENABLED", userRoleUris);
    }

    @JsonIgnore
    public String getEmail() {
        return content.getEmail();
    }

    @JsonIgnore
    public String getStatus() {
        return content.getStatus();
    }

    @JsonIgnore
    public String getLastName() {
        return content.getLastName();
    }

    @JsonIgnore
    public List<String> getUserRoles() {
        return content.getUserRoles();
    }

    @JsonIgnore
    public String getLogin() {
        return content.getLogin();
    }

    @JsonIgnore
    public String getFirstName() {
        return content.getFirstName();
    }

    @JsonIgnore
    public String getPhoneNumber() {
        return content.getPhoneNumber();
    }

    public Links getLinks() {
        return links;
    }

    public void setStatus(final String status) {
        this.content.status = status;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class UserContent {

        @JsonProperty("email")
        private String email;

        @JsonProperty("firstname")
        private String firstName;

        @JsonProperty("userRoles")
        private List<String> userRoles;

        @JsonProperty("phonenumber")
        private String phoneNumber;

        @JsonProperty("status")
        private String status;

        @JsonProperty("lastname")
        private String lastName;

        @JsonProperty("login")
        private String login;

        @JsonCreator
        public UserContent(@JsonProperty("email") final String email,
                           @JsonProperty("firstname") final String firstName,
                           @JsonProperty("userRoles") final List<String> userRoles,
                           @JsonProperty("phonenumber") final String phoneNumber,
                           @JsonProperty("status") final String status,
                           @JsonProperty("lastname") final String lastName,
                           @JsonProperty("login") final String login) {
            this.email = email;
            this.firstName = firstName;
            this.userRoles = userRoles;
            this.phoneNumber = phoneNumber;
            this.status = status;
            this.lastName = lastName;
            this.login = login;
        }

        private UserContent(final String status, final List<String> userRoles) {
            this.userRoles = userRoles;
            this.status = status;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public List<String> getUserRoles() {
            return userRoles;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public String getStatus() {
            return status;
        }

        public String getLastName() {
            return lastName;
        }

        public String getLogin() {
            return login;
        }

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private static class Links {

        private String self;

        private Links(@JsonProperty("self") final String self) {
            this.self = self;
        }

        public String getSelf() {
            return self;
        }
    }
}
