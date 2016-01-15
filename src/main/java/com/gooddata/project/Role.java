package com.gooddata.project;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.md.Meta;
import com.gooddata.util.BooleanStringDeserializer;
import com.gooddata.util.BooleanStringSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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

    @JsonDeserialize(contentUsing = BooleanStringDeserializer.class)
    @JsonSerialize(contentUsing = BooleanStringSerializer.class)
    private final Map<String, Boolean> permissions;

    private Meta meta;

    private final Map<String, String> links;

    @JsonCreator
    Role(@JsonProperty("permissions") final Map<String, Boolean> permissions,
         @JsonProperty("meta") final Meta meta,
         @JsonProperty("links") final Map<String, String> links) {
        this.permissions = permissions == null ? new HashMap<String, Boolean>() : permissions;
        this.meta = meta == null ? new Meta(null) : meta;
        this.links = links == null ? new HashMap<String, String>() : links;
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
        final Set<String> permissions = new HashSet<>();
        for (Entry<String, Boolean> entry : this.permissions.entrySet()) {
            if (entry.getValue()) {
                permissions.add(entry.getKey());
            }
        }
        return permissions;
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
}
