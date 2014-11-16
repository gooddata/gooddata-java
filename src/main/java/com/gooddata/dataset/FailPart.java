package com.gooddata.dataset;

import com.gooddata.gdc.ErrorStructure;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Describe status of uploaded parts
 */
public class FailPart {

    private final String logName;
    private final String fileName;
    private final String status;
    private final String tableName;
    private final ErrorStructure error;

    @JsonCreator
    private FailPart(@JsonProperty("logName") String logName, @JsonProperty("fileName") String fileName,
                     @JsonProperty("status") String status, @JsonProperty("tableName") String tableName,
                     @JsonProperty("error") ErrorStructure error) {
        this.logName = logName;
        this.fileName = fileName;
        this.status = status;
        this.tableName = tableName;
        this.error = error;
    }

    public String getLogName() {
        return logName;
    }

    public String getFileName() {
        return fileName;
    }

    public String getStatus() {
        return status;
    }

    public String getTableName() {
        return tableName;
    }

    public ErrorStructure getError() {
        return error;
    }
}
