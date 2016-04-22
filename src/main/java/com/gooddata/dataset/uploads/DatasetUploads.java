package com.gooddata.dataset.uploads;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.dataset.UploadStatus;
import com.gooddata.util.GDDateTimeDeserializer;
import org.joda.time.DateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetUploads {

        private final Meta meta;
        private final String dataUploadsUri;
        private final DateTime lastSuccess;
        private final LastUpload lastUpload;

        private DatasetUploads(
                @JsonProperty("meta") Meta meta,
                @JsonProperty("dataUploads") String dataUploadsUri,
                @JsonProperty("lastSuccess") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime lastSuccess,
                @JsonProperty("lastUpload") LastUpload lastUpload) {
            this.meta = meta;
            this.dataUploadsUri = dataUploadsUri;
            this.lastSuccess = lastSuccess;
            this.lastUpload = lastUpload;
        }

        public String getDatasetId() {
            return meta.datasetId;
        }

        public String getUploadsUri() {
            return dataUploadsUri;
        }

        public DateTime getLastSuccess() {
            return lastSuccess;
        }

        public LastUpload getLastUpload() {
            return lastUpload;
        }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class Meta {
        private final String datasetId;

        private Meta(@JsonProperty("identifier") String datasetId) {
            this.datasetId = datasetId;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeName("dataUploadShort")
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    public static class LastUpload extends AbstractUpload {

        private final DateTime date;

        private LastUpload(
                @JsonProperty("uri") String uri,
                @JsonProperty("status") UploadStatus status,
                @JsonProperty("progress") Double progress,
                @JsonProperty("msg") String message,
                @JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime date) {
            super(uri, status, progress, message);
            this.date = date;
        }

        public DateTime getDate() {
            return date;
        }
    }
}
