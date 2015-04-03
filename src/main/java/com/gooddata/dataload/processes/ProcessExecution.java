package com.gooddata.dataload.processes;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;

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
        this(process, executable, new HashMap<String, String>(), new HashMap<String, String>());
    }

    public ProcessExecution(DataloadProcess process, String executable, Map<String, String> params) {
        this(process, executable, params, new HashMap<String, String>());
    }

    public ProcessExecution(DataloadProcess process, String executable, Map<String, String> params, Map<String, String> hiddenParams) {
        notNull(process, "process");
        this.executionsUri = notEmpty(process.getExecutionsLink(), "process executions link");
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
    String getExecutionsUri() {
        return executionsUri;
    }
}
