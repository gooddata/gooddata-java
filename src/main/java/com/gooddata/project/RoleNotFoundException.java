package com.gooddata.project;

import com.gooddata.GoodDataException;

/**
 * Project role of the given URI doesn't exist
 */
public class RoleNotFoundException extends GoodDataException {

    private final String uri;

    public RoleNotFoundException(String uri, Throwable cause) {
        super("Role " + uri + " was not found", cause);
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }
}
