package com.gooddata.warehouse;

import static com.gooddata.Validate.notNull;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriTemplate;

import java.util.Map;

/**
 * Warehouse
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("instance")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Warehouse {

    private static final String ID_PARAM = "id";
    public static final String URI = Warehouses.URI + "/{" + ID_PARAM + "}";

    public static final UriTemplate TEMPLATE = new UriTemplate(URI);
    public static final UriTemplate JDBC_CONNECTION_TEMPLATE = new UriTemplate("jdbc:gdc:datawarehouse://{host}:{port}/gdc/datawarehouse/instances/{id}");

    private static final String SELF_LINK = "self";
    private static final String STATUS_ENABLED = "ENABLED";

    private String title;
    private String description;

    private String authorizationToken;
    private String created; // TODO should be date
    private String updated; // TODO should be date
    private String createdBy;
    private String updatedBy;
    private String status;
    private Map<String, String> links;

    private String warehouseHost;
    private int warehousePort = 443;

    public Warehouse(String title, String authToken) {
        this(title, authToken, null);
    }

    public Warehouse(String title, String authToken, String description) {
        this.title = notNull(title, "title");
        this.authorizationToken = notNull(authToken, "authToken");
        this.description = description;
    }

    @JsonCreator
    Warehouse(@JsonProperty("title") String title, @JsonProperty("authorizationToken") String authToken,
            @JsonProperty("description") String description, @JsonProperty("created") String created, @JsonProperty("updated") String updated,
            @JsonProperty("createdBy") String createdBy, @JsonProperty("updatedBy") String updatedBy, @JsonProperty("status") String status, @JsonProperty("links") Map<String, String> links) {
        this(title, authToken, description);
        this.created = created;
        this.updated = updated;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.status = status;
        this.links = links;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public String getStatus() {
        return status;
    }

    void setWarehouseHost(String warehouseHost) {
        this.warehouseHost = warehouseHost;
    }

    void setWarehousePort(int warehousePort) {
        this.warehousePort = warehousePort;
    }

    /**
     * Get jdbc connection string. Works only on Warehouse loaded from API (using WarehouseService).
     * @return jdbc connection string
     */
    @JsonIgnore
    public String getJdbcConnectionString() {
        if (warehouseHost == null) {
            throw new IllegalStateException("Please set warehouseHost " +
                    "to be able to construct jdbc connection string");
        }
        return JDBC_CONNECTION_TEMPLATE.expand(warehouseHost, warehousePort, getId()).toString();
    }

    @JsonIgnore
    public String getUri() {
        return links != null ? links.get(SELF_LINK): null;
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get(ID_PARAM);
    }

    @JsonIgnore
    public boolean isEnabled() {
        return STATUS_ENABLED.equals(status);
    }
}
