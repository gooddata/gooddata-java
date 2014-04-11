package com.gooddata.dataset;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeName;
import java.util.List;
import java.util.Map;

/**
 * TODO
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("dataSetSLIManifest")
public class DatasetManifest {

    public static final String URI = "/gdc/md/{projectId}/ldm/singleloadinterface/{id}/manifest";

    private final String id;
    private String file;
    private List<Part> parts;

    public DatasetManifest(String id) {
        this.id = id;
    }

    @JsonCreator
    public DatasetManifest(@JsonProperty("dataSet") String id, @JsonProperty("file") String file, @JsonProperty("parts") List<Part> parts) {
        this.id = id;
        this.file = file;
        this.parts = parts;
    }

    public String getId() {
        return id;
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

    public static class Part {

        @JsonProperty("mode")
        private String uploadMode;
        private String columnName;
        private List<String> populates;
        private Integer referenceKey;
        private Map<String, String> constraints;

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

        public Integer getReferenceKey() {
            return referenceKey;
        }

        public void setReferenceKey(Integer referenceKey) {
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
