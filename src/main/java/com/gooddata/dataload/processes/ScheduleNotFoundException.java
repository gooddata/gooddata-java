package com.gooddata.dataload.processes;

import com.gooddata.GoodDataException;

/**
 * Schedule of the given URI doesn't exist
 */
public class ScheduleNotFoundException extends GoodDataException {

    private final String uri;

    public ScheduleNotFoundException(String uri, Throwable cause) {
        super("Schedule " + uri + " was not found", cause);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
