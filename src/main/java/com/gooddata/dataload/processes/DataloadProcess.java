package com.gooddata.dataload.processes;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.util.CollectionUtils;
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
public class DataloadProcess {

    public static final String URI = "/gdc/projects/{projectId}/dataload/processes/{processId}";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    private static final String SELF_LINK = "self";
    private static final String EXECUTIONS_LINK = "executions";

    private String name;
    private String type;
    private Set<String> executables;
    private Map<String,String> links;
    private String path;

    public DataloadProcess(String name, String type) {
        this.name = notEmpty(name, "name");
        this.type = notEmpty(type, "type");
    }

    DataloadProcess(String name, String type, String path) {
        this(name, type);
        this.path = path;
    }

    public DataloadProcess(String name, ProcessType type) {
        this(name, notNull(type, "type").toString());
    }

    @JsonCreator
    private DataloadProcess(@JsonProperty("name") String name, @JsonProperty("type") String type,
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

    public String getPath() {
        return path;
    }

    void setPath(String path) {
        this.path = path;
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

    public void validateExecutable(final String executable) {
        if (!CollectionUtils.isEmpty(getExecutables()) &&
                !getExecutables().contains(executable)) {
            throw new IllegalArgumentException("Executable " + executable + " not found in process executables " + getExecutables());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof DataloadProcess))
            return false;

        DataloadProcess that = (DataloadProcess) o;

        if (getUri() != null ? !getUri().equals(that.getUri()) : that.getUri() != null)
            return false;
        if (executables != null ? !executables.equals(that.executables) : that.executables != null)
            return false;
        if (!name.equals(that.name))
            return false;
        if (!type.equals(that.type))
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + (executables != null ? executables.hashCode() : 0);
        result = 31 * result + (getUri() != null ? getUri().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DataloadProcess{");
        sb.append("name='").append(name).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", executables=").append(executables);
        sb.append(", uri=").append(getUri());
        sb.append('}');
        return sb.toString();
    }
}
