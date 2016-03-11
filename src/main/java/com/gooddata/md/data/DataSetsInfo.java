package com.gooddata.md.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.md.AbstractObj;
import com.gooddata.md.Meta;
import com.gooddata.util.GDDateTimeDeserializer;

import org.joda.time.DateTime;
import org.springframework.web.util.UriTemplate;

import java.util.List;

/**
 * Basic information about datasets, dataset uploads, ...
 * Deserialization only.
 */
@JsonTypeName("dataSetsInfo")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetsInfo {

    public static final String URI = "/gdc/md/{projectId}/data/sets";
    public static final UriTemplate URI_TEMPLATE = new UriTemplate(URI);

    private final List<Dataset> datasets;

    private DatasetsInfo(@JsonProperty("sets") List<Dataset> datasets) {
        this.datasets = datasets;
    }

    public List<Dataset> getDatasets() {
        return datasets;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Dataset extends AbstractObj {

        private final String etlInterfaceUri;
        private final String dataUploadsUri;
        private final String uploadConfigurationUri;
        private final DateTime lastSuccess;
        private final DataUploadShort lastUpload;

        protected Dataset(
                @JsonProperty("meta") Meta meta,
                @JsonProperty("etlInterface") String etlInterfaceUri,
                @JsonProperty("dataUploads") String dataUploadsUri,
                @JsonProperty("lastSuccess") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime lastSuccess,
                @JsonProperty("uploadConfiguration") String uploadConfigurationUri,
                @JsonProperty("lastUpload") DataUploadShort lastUpload) {
            super(meta);
            this.etlInterfaceUri = etlInterfaceUri;
            this.dataUploadsUri = dataUploadsUri;
            this.uploadConfigurationUri = uploadConfigurationUri;
            this.lastSuccess = lastSuccess;
            this.lastUpload = lastUpload;
        }

        public String getEtlInterfaceUri() {
            return etlInterfaceUri;
        }

        public String getDataUploadsUri() {
            return dataUploadsUri;
        }

        public String getUploadConfigurationUri() {
            return uploadConfigurationUri;
        }

        public DateTime getLastSuccess() {
            return lastSuccess;
        }

        @JsonIgnore
        public String getLastUploadUri() {
            return lastUpload != null ? lastUpload.getUri() : null;
        }

        public DataUploadStatus getLastUploadStatus() {
            return lastUpload != null ? lastUpload.getStatus() : null;
        }

        /**
         * @return last upload progress in percent as floating point number or {@code 0} if the last upload does not exist
         */
        public float getLastUploadProgress() {
            return lastUpload != null ? lastUpload.getProgress() : 0;
        }

        public String getLastUploadMessage() {
            return lastUpload != null ? lastUpload.getMessage() : null;
        }

        public DateTime getLastUploadDate() {
            return lastUpload != null ? lastUpload.date : null;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeName("dataUploadShort")
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    private static class DataUploadShort extends AbstractDataUpload {

        private final DateTime date;

        protected DataUploadShort(
                @JsonProperty("uri") String uri,
                @JsonProperty("status") DataUploadStatus status,
                @JsonProperty("progress") float progress,
                @JsonProperty("msg") String message,
                @JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime date) {
            super(uri, status, progress, message);
            this.date = date;
        }
    }
}
