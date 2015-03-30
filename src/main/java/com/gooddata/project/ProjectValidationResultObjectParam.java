package com.gooddata.project;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeName;

@JsonTypeName("object")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectValidationResultObjectParam extends ProjectValidationResultParam {
    private final String name;
    private final String uri;

    ProjectValidationResultObjectParam(String name, String uri) {
        this.name = name;
        this.uri = uri;
    }

    @JsonCreator
    private static ProjectValidationResultObjectParam create(@JsonProperty("name") String name, @JsonProperty("uri") String uri) {
        return new ProjectValidationResultObjectParam(name, uri);
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public String toString() {
        return name + " (" + uri + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProjectValidationResultObjectParam that = (ProjectValidationResultObjectParam) o;

        if (!name.equals(that.name)) return false;
        if (!uri.equals(that.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + uri.hashCode();
        return result;
    }
}
