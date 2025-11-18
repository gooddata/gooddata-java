/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.webdav;

import java.io.InputStream;
import java.util.Map;

/**
 * WebDAV response containing operation result and metadata.
 */
public class WebDavResponse {
    
    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private final InputStream content;
    private final boolean success;
    private final String errorMessage;

    private WebDavResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.headers = builder.headers;
        this.content = builder.content;
        this.success = builder.success;
        this.errorMessage = builder.errorMessage;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getContent() {
        return content;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int statusCode;
        private String statusMessage;
        private Map<String, String> headers = Map.of();
        private InputStream content;
        private boolean success;
        private String errorMessage;

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            this.success = statusCode >= 200 && statusCode < 300;
            return this;
        }

        public Builder statusMessage(String statusMessage) {
            this.statusMessage = statusMessage;
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers != null ? headers : Map.of();
            return this;
        }

        public Builder content(InputStream content) {
            this.content = content;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public WebDavResponse build() {
            return new WebDavResponse(this);
        }
    }
}