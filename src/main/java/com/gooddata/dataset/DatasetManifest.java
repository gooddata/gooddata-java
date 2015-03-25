/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.util.BooleanIntegerDeserializer;
import com.gooddata.util.BooleanIntegerSerializer;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;

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
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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
