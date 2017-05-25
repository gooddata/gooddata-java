/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gooddata.util.BooleanDeserializer;
import com.gooddata.util.BooleanIntegerSerializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.util.GoodDataToStringBuilder;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;


/**
 * Dataset specific upload manifest
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("dataSetSLIManifest")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DatasetManifest {

    public static final String URI = "/gdc/md/{projectId}/ldm/singleloadinterface/{dataSet}/manifest";

    private final String dataSet;
    private String file;
    private List<Part> parts;
    private InputStream source;

    public DatasetManifest(String dataSet) {
        this.dataSet = dataSet;
    }

    /**
     * Create dataset upload manifest.
     * @param dataSet dataset name
     * @param source  source CSV
     */
    public DatasetManifest(final String dataSet, final InputStream source) {
        this.source = notNull(source, "source");
        this.dataSet = notEmpty(dataSet, "dataSet");
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
        this.file = notEmpty(file, "file");
    }

    /**
     * Set upload mode for all parts of this dataset manifest
     * @param uploadMode upload mode
     */
    public void setUploadMode(final UploadMode uploadMode) {
        notNull(uploadMode, "uploadMode");
        for (Part part : parts) {
            part.setUploadMode(uploadMode.name());
        }
    }

    /**
     * Map the given CSV column name to the dataset field
     * @param columnName column name
     * @param populates dataset field
     */
    public void setMapping(final String columnName, final String populates) {
        notNull(columnName, "columnName");
        notNull(populates, "populates");
        for (Part part : parts) {
            if (part.getPopulates() == null || part.getPopulates().size() != 1) {
                throw new IllegalStateException("Only parts with exactly one populates are supported " + part.getPopulates());
            }
            final String field = part.getPopulates().iterator().next();
            if (populates.equals(field)) {
                part.setColumnName(columnName);
                return;
            }
        }
        throw new IllegalArgumentException("Dataset manifest parts doesn't contain populate value " + populates);
    }

    @JsonIgnore
    public InputStream getSource() {
        return source;
    }

    @JsonIgnore
    public void setSource(final InputStream source) {
        this.source = notNull(source, "source");
    }

    @Override
    public String toString() {
        return GoodDataToStringBuilder.defaultToString(this, "source");
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class Part {

        @JsonProperty("mode")
        private String uploadMode;
        private String columnName;
        private List<String> populates;
        private Boolean referenceKey;
        private Map<String, String> constraints;

        @JsonCreator
        public Part(@JsonProperty("mode") String uploadMode,
             @JsonProperty("columnName") String columnName,
             @JsonProperty("populates") List<String> populates,
             @JsonProperty("referenceKey") @JsonDeserialize(using = BooleanDeserializer.class) Boolean referenceKey,
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

        /**
         * @return true if the referenceKey is set and is true
         */
        @JsonIgnore
        public boolean isReferenceKey() {
            return Boolean.TRUE.equals(referenceKey);
        }

        @JsonSerialize(using = BooleanIntegerSerializer.class)
        public Boolean getReferenceKey() {
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

        @Override
        public String toString() {
            return GoodDataToStringBuilder.defaultToString(this);
        }
    }
}
