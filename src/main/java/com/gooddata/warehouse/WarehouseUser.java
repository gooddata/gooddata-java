package com.gooddata.warehouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

import static com.gooddata.util.Validate.notNull;

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
    public static final String ADMIN_ROLE = "admin";
    public static final String DATA_ADMIN_ROLE = "dataAdmin";

    private static final String SELF_LINK = "self";

    private String role;
    private String profile;
    private String login;
    private Map<String, String> links;

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
        return links != null ? links.get(SELF_LINK): null;
    }

}
