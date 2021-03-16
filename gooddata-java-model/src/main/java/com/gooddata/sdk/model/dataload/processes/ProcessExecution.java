/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.dataload.processes;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.gooddata.sdk.common.util.GoodDataToStringBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Dataload process execution. Serialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("execution")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessExecution {

    private final String executionsUri;

    private final String executable;
    private final Map<String,String> params;
    private final Map<String,String> hiddenParams;

    public ProcessExecution(DataloadProcess process, String executable) {
        this(process, executable, new HashMap<>(), new HashMap<>());
    }

    public ProcessExecution(DataloadProcess process, String executable, Map<String, String> params) {
        this(process, executable, params, new HashMap<>());
    }

    public ProcessExecution(DataloadProcess process, String executable, Map<String, String> params, Map<String, String> hiddenParams) {
        notNull(process, "process");
        this.executionsUri = notEmpty(process.getExecutionsUri(), "process executions link");
        this.executable = executable;
        this.params = notNull(params, "params");
        this.hiddenParams = notNull(hiddenParams, "hiddenParams");

        process.validateExecutable(executable);
    }

    public String getExecutable() {
        return executable;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Map<String, String> getHiddenParams() {
        return hiddenParams;
    }

    @JsonIgnore
    public String getExecutionsUri() {
        return executionsUri;
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this, "hiddenParams", "executionsUri");
    }
}
