/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.webdav;

import java.io.InputStream;
import java.util.Map;

/**
 * WebDAV request containing all necessary information for WebDAV operations.
 */
public class WebDavRequest {
    
    private final String url;
    private final String method;
    private final Map<String, String> headers;
    private final InputStream content;
    private final long contentLength;

    private WebDavRequest(Builder builder) {
        this.url = builder.url;
        this.method = builder.method;
        this.headers = builder.headers;
        this.content = builder.content;
        this.contentLength = builder.contentLength;
    }

    public String getUrl() {
        return url;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public InputStream getContent() {
        return content;
    }

    public long getContentLength() {
        return contentLength;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String url;
        private String method = "GET";
        private Map<String, String> headers = Map.of();
        private InputStream content;
        private long contentLength = -1;

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
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

        public Builder contentLength(long contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public WebDavRequest build() {
            if (url == null || url.trim().isEmpty()) {
                throw new IllegalArgumentException("URL is required");
            }
            return new WebDavRequest(this);
        }
    }
}