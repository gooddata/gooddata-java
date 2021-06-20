/*
 * Copyright (C) 2007-2021, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.github.sardine.impl.SardineException;

/**
 * Extended Sardine exception about X-GDC-REQUEST header value.
 */
public class GdcSardineException extends SardineException {

    private final String requestId;

    /**
     * @param msg            Custom description of failure
     * @param statusCode     Error code returned by server
     * @param responsePhrase Response phrase following the error code
     * @param requestId      The X-GDC-REQUEST identifier.
     */
    public GdcSardineException(String requestId, String msg, int statusCode, String responsePhrase) {
        super(msg, statusCode, responsePhrase);
        this.requestId = requestId;
    }

    @Override
    public String getMessage() {
        return String.format("[request_id=%s]: %s (%d %s)", this.requestId, super.getMessage(), this.getStatusCode(),
                this.getResponsePhrase()
        );
    }

}
