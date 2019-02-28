/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.model.account.Account;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

import static com.gooddata.util.Validate.notNull;
import static com.gooddata.util.Validate.notNullState;

/**
 * Warehouse user
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("user")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class WarehouseUser {

    public static final String URI = WarehouseUsers.URI + "/{userId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";

    private final String role;
    private final String profile;
    private final String login;
    private Map<String, String> links;

    /**
     * Creates a new instance with given parameters.
     * <p>
     * Only one of the parameters <code>profile</code> and <code>login</code> must be provided.
     *
     * @param role    role of the user in ADS
     * @param profile profileId (<pre>/gdc/account/profile/{id}</pre>) of the user
     * @param login   login of the user
     */
    public WarehouseUser(final String role, final String profile, final String login) {
        this.role = notNull(role, "role");
        if (profile == null) {
            notNull(login, "login");
        }
        if (login == null) {
            notNull(profile, "profile");
        }
        this.profile = profile;
        this.login = login;
    }

    /**
     * Creates a new {@link WarehouseUser} with <code>role</code> and <code>profileUri</code> set
     */
    public static WarehouseUser createWithProfileUri(final String profileUri, final WarehouseUserRole role) {
        notNull(role, "role cannot be null");
        return new WarehouseUser(role.getRoleName(), profileUri, null);
    }

    /**
     * Creates a new {@link WarehouseUser} with <code>role</code> and <code>profile</code> set
     */
    public static WarehouseUser createWithProfile(final Account profile, final WarehouseUserRole role) {
        notNull(role, "role cannot be null");
        notNull(profile, "profile cannot be null");
        return new WarehouseUser(role.getRoleName(), profile.getId(), null);
    }

    /**
     * Creates a new {@link WarehouseUser} with <code>role</code> and <code>login</code> set
     */
    public static WarehouseUser createWithlogin(final String login, final WarehouseUserRole role) {
        notNull(role, "role cannot be null");
        return new WarehouseUser(role.getRoleName(), null, login);
    }

    @JsonCreator
    public WarehouseUser(@JsonProperty("role") String role, @JsonProperty("profile") String profile,
                         @JsonProperty("login") String login, @JsonProperty("links") Map<String, String> links) {
        this(role, profile, login);
        this.links = links;
    }

    public String getRole() {
        return role;
    }

    public String getProfile() {
        return profile;
    }

    public String getLogin() {
        return login;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get("userId");
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
