/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.util.BooleanIntegerDeserializer;
import com.gooddata.util.BooleanIntegerSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.util.List;
import java.util.Map;

/**
 * Dataset specific upload manifest
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("dataSetSLIManifest")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class DatasetManifest {

    public static final String URI = "/gdc/md/{projectId}/ldm/singleloadinterface/{dataSet}/manifest";

    private final String dataSet;
    private String file;
    private List<Part> parts;

    public DatasetManifest(String dataSet) {
        this.dataSet = dataSet;
    }

    @JsonCreator
    public DatasetManifest(@JsonProperty("dataSet") String dataSet, @JsonProperty("file") String file,
                           @JsonProperty("parts") List<Part> parts) {
        this.dataSet = dataSet;
        this.file = file;
        this.parts = parts;
    }

    public String getDataSet() {
        return dataSet;
    }

    public List<Part> getParts() {
        return parts;
    }

    public void setParts(List<Part> parts) {
        this.parts = parts;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public static class Part {

        @JsonProperty("mode")
        private String uploadMode;
        private String columnName;
        private List<String> populates;
        private boolean referenceKey;
        private Map<String, String> constraints;

        @JsonCreator
        Part(@JsonProperty("mode") String uploadMode,
             @JsonProperty("columnName") String columnName,
             @JsonProperty("populates") List<String> populates,
             @JsonProperty("referenceKey") @JsonDeserialize(using = BooleanIntegerDeserializer.class) boolean referenceKey,
             @JsonProperty("constraints") Map<String, String> constraints) {
            this.uploadMode = uploadMode;
            this.columnName = columnName;
            this.populates = populates;
            this.referenceKey = referenceKey;
            this.constraints = constraints;
        }

        public String getUploadMode() {
            return uploadMode;
        }

        public void setUploadMode(String uploadMode) {
            this.uploadMode = uploadMode;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public List<String> getPopulates() {
            return populates;
        }

        public void setPopulates(List<String> populates) {
            this.populates = populates;
        }

	    @JsonSerialize(using = BooleanIntegerSerializer.class)
        public boolean isReferenceKey() {
            return referenceKey;
        }

        public void setReferenceKey(boolean referenceKey) {
            this.referenceKey = referenceKey;
        }

        public Map<String, String> getConstraints() {
            return constraints;
        }

        public void setConstraints(Map<String, String> constraints) {
            this.constraints = constraints;
        }
    }
}
