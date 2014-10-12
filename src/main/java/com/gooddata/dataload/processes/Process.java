package com.gooddata.dataload.processes;

import static com.gooddata.Validate.notEmpty;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Dataload process.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("process")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Process {

    public static final String URI = "/gdc/projects/{projectId}/dataload/processes/{processId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";
    private static final String EXECUTIONS_LINK = "executions";

    private String name;
    private String type;
    private Set<String> executables;
    private Map<String,String> links;

    public Process(String name, String type) {
        this.name = notEmpty(name, "name");
        this.type = notEmpty(type, "type");
    }

    @JsonCreator
    private Process(@JsonProperty("name") String name, @JsonProperty("type") String type,
            @JsonProperty("executables") Set<String> executables,
            @JsonProperty("links") Map<String, String> links) {
        this(name, type);
        this.executables = executables != null ? Collections.unmodifiableSet(executables) : null;
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonIgnore
    public Set<String> getExecutables() {
        return executables;
    }

    @JsonIgnore
    public String getUri() {
        return links != null ? links.get(SELF_LINK) : null;
    }

    @JsonIgnore
    public String getId() {
        return TEMPLATE.match(getUri()).get("processId");
    }

    @JsonIgnore
    public String getExecutionsLink() {
        return links != null ? links.get(EXECUTIONS_LINK) : null;
    }

    @JsonIgnore
    public String getSourceLink() {
        return getUri() != null ? getUri() + "/source" : null;
    }
}
