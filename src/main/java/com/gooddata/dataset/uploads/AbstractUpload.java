package com.gooddata.dataset.uploads;


import com.gooddata.dataset.UploadStatus;

/**
 * Abstract parent for Upload classes with minimal upload info (internal use only)
 */
abstract class AbstractUpload {

    private final String uri;
    private final UploadStatus status;
    private final double progress;
    private final String message;

    protected AbstractUpload(
            String uri,
            UploadStatus status,
            Double progress,
            String message) {
        this.uri = uri;
        this.status = status;
        this.progress = progress != null ? progress : 0;
        this.message = message;
    }

    public String getUri() {
        return uri;
    }

    public UploadStatus getStatus() {
        return status;
    }

    /**
     * @return upload progress in percent as floating point number
     */
    public double getProgress() {
        return progress;
    }

    public String getMessage() {
        return message;
    }
}
