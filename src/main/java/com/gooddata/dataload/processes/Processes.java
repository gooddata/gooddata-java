package com.gooddata.dataload.processes;

import com.gooddata.account.Account;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.springframework.web.util.UriTemplate;

import java.util.Collection;
import java.util.List;

/**
 * List of processes. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("processes")
@JsonIgnoreProperties(ignoreUnknown = true)
class Processes {
    public static final String URI = "/gdc/projects/{projectId}/dataload/processes";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    public static final String USER_PROCESSES_URI = Account.URI + "/dataload/processes";
    public static final UriTemplate USER_PROCESSES_TEMPLATE = new UriTemplate(USER_PROCESSES_URI);

    private final List<DataloadProcess> items;

    @JsonCreator
    private Processes(@JsonProperty("items") List<DataloadProcess> items) {
        this.items = items;
    }

    Collection<DataloadProcess> getItems() {
        return items;
    }
}
