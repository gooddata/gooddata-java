/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.md.Meta;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.BooleanStringSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.GoodDataToStringBuilder;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Project Role
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("projectRole")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {
    public static final String URI = "/gdc/projects/{projectId}/roles/{roleId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";

    @JsonDeserialize(contentUsing = BooleanDeserializer.class)
    @JsonSerialize(contentUsing = BooleanStringSerializer.class)
    private final Map<String, Boolean> permissions;

    private final Meta meta;

    private final Map<String, String> links;

    @JsonCreator
    Role(@JsonProperty("permissions") final Map<String, Boolean> permissions,
         @JsonProperty("meta") final Meta meta,
         @JsonProperty("links") final Map<String, String> links) {
        this.permissions = permissions == null ? new HashMap<>() : permissions;
        this.meta = meta == null ? new Meta(null) : meta;
        this.links = links == null ? new HashMap<>() : links;
    }

    /**
     * Returns set of permission names this role can have granted.
     *
     * @return set of permission names
     */
    public Set<String> getPermissions() {
        return Collections.unmodifiableSet(permissions.keySet());
    }

    /**
     * Returns names of granted permissions.
     *
     * @return set of granted permissions
     */
    public Set<String> getGrantedPermissions() {
        return permissions.entrySet().stream().filter(Entry::getValue).map(Entry::getKey).collect(Collectors.toSet());
    }

    /**
     * Returns <code>true</code> if provided permission is granted.
     *
     * @param permission permission name to test
     * @return whether the permission is granted
     */
    public boolean hasPermissionGranted(final String permission) {
        return permissions.get(permission);
    }

    public String getTitle() {
        return meta.getTitle();
    }

    public String getIdentifier() {
        return meta.getIdentifier();
    }

    /**
     * Allows service to set self link as it is not provided by REST API.
     * <p>
     * NOTE: This is intentionally left package-private.
     */
    void setUri(final String uri) {
        links.put(SELF_LINK, uri);
    }

    @JsonIgnore
    public String getUri() {
        return links.get(SELF_LINK);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Role role = (Role) o;

        if (!permissions.equals(role.permissions)) return false;
        return !(getIdentifier() != null ? !getIdentifier().equals(role.getIdentifier()) : role.getIdentifier() != null);

    }

    @Override
    public int hashCode() {
        int result = permissions.hashCode();
        result = 31 * result + (getIdentifier() != null ? getIdentifier().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
