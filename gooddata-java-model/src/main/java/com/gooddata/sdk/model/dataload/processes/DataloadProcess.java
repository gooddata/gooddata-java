/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import com.fasterxml.jackson.annotation.*;
import com.gooddata.sdk.model.util.UriHelper;
import com.gooddata.util.GoodDataToStringBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.gooddata.util.Validate.*;

/**
 * Dataload process.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("process")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DataloadProcess {

    public static final String URI = "/gdc/projects/{projectId}/dataload/processes/{processId}";

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

    /**
     * Use this constructor, when you want to deploy process from appstore.
     *
     * @param name name
     * @param type type
     * @param appstorePath valid path to brick in appstore
     */
    public DataloadProcess(String name, String type, String appstorePath) {
        this(name, type);
        this.path = appstorePath;
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

    public void setPath(String path) {
        this.path = path;
    }

    @JsonIgnore
    public Set<String> getExecutables() {
        return executables;
    }

    @JsonIgnore
    public String getUri() {
        return notNullState(links, "links").get(SELF_LINK);
    }

    @JsonIgnore
    public String getId() {
        return UriHelper.getLastUriPart(getUri());
    }

    @JsonIgnore
    public String getExecutionsUri() {
        return notNullState(links, "links").get(EXECUTIONS_LINK);
    }

    @JsonIgnore
    public String getSourceUri() {
        return getUri() + "/source";
    }

    public void validateExecutable(final String executable) {
        if (getExecutables() != null && !getExecutables().isEmpty() &&
                !getExecutables().contains(executable)) {
            throw new IllegalArgumentException("Executable " + executable + " not found in process executables " + getExecutables());
        }
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this);
    }
}
