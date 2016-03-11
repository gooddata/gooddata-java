package com.gooddata.md.data;


import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Abstract parent for DataUpload classes with minimal upload info (internal use only)
 */
abstract class AbstractDataUpload {

    private final String uri;
    private final DataUploadStatus status;
    private final float progress;
    private final String message;

    protected AbstractDataUpload(
            @JsonProperty("uri") String uri,
            @JsonProperty("status") DataUploadStatus status,
            @JsonProperty("progress") float progress,
            @JsonProperty("msg") String message) {
        this.uri = uri;
        this.status = status;
        this.progress = progress;
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public DataUploadStatus getStatus() {
        return status;
    }

    /**
     * @return upload progress in percent as floating point number
     */
    public float getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }
}
