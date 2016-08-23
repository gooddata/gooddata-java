package com.gooddata.dataset;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gooddata.md.Meta;
import com.gooddata.util.GDDateTimeDeserializer;
import org.joda.time.DateTime;

/**
 * Contains information about dataset uploads for one dataset in the project. Information contain:
 * <ul>
 *      <li>dataset ID</li>
 *      <li>uri to all uploads for the given dataset</li>
 *      <li>date of the last successful upload</li>
 *      <li>last upload</li>
 * </ul>
 *
 * Deserialization only.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DatasetUploadsInfo {

    private final Meta meta;
    private final String datasetUploadsUri;
    private final DateTime lastSuccess;
    private final LastUpload lastUpload;

    private DatasetUploadsInfo(
            @JsonProperty("meta") Meta meta,
            @JsonProperty("dataUploads") String datasetUploadsUri,
            @JsonProperty("lastSuccess") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime lastSuccess,
            @JsonProperty("lastUpload") LastUpload lastUpload) {
        this.meta = meta;
        this.datasetUploadsUri = datasetUploadsUri;
        this.lastSuccess = lastSuccess;
        this.lastUpload = lastUpload;
    }

    /**
     * @return dataset identifier
     */
    public String getDatasetId() {
        return meta.getIdentifier();
    }

    /**
     * @return dataset's author
     */
    public String getDatasetAuthor() {
        return meta.getAuthor();
    }

    /**
     * @return dataset created date
     */
    public DateTime getDatasetCreated() {
        return meta.getCreated();
    }

    /**
     * @return dataset title
     */
    public String getDatasetTitle() {
        return meta.getTitle();
    }

    /**
     * @return dataset URI link
     */
    public String getDatasetUri() {
        return meta.getUri();
    }

    /**
     * @return dataset's summary
     */
    public String getDatasetSummary() {
        return meta.getSummary();
    }

    /**
     * @return URI for all uploads of this dataset
     */
    public String getUploadsUri() {
        return datasetUploadsUri;
    }

    /**
     * @return date of the last successful upload of this dataset
     */
    public DateTime getLastSuccess() {
        return lastSuccess;
    }

    /**
     * Returns object containing information about last dataset upload. This upload doesn't contain information
     * about upload mode, size of data and date when this upload was processed. If you want to get more information about
     * dataset uploads, use {@link DatasetService#listUploadsForDataset(DatasetUploadsInfo)} method.
     *
     * @return {@link Upload} object
     */
    public Upload getLastUpload() {
        return lastUpload.upload;
    }

    /**
     * Wrapper class for the last upload.
     * Only for deserialization. For internal use only.
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonTypeName("dataUploadShort")
    @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
    private static class LastUpload {

        private final Upload upload;

        private LastUpload(
                @JsonProperty("uri") String uri,
                @JsonProperty("status") String status,
                @JsonProperty("progress") Double progress,
                @JsonProperty("msg") String message,
                @JsonProperty("date") @JsonDeserialize(using = GDDateTimeDeserializer.class) DateTime date) {

            this.upload = new Upload(message, progress, status, null, uri, date, null, null);
        }
    }
}
