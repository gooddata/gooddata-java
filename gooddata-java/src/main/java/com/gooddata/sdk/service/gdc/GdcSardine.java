/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import static com.gooddata.sdk.common.util.Validate.notNull;

import com.github.sardine.impl.SardineImpl;
import com.github.sardine.impl.io.ContentLengthInputStream;
import com.github.sardine.impl.io.HttpMethodReleaseInputStream;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * This class extends SardineImpl, connections were not correctly closed by parent
 */
class GdcSardine extends SardineImpl {

    public GdcSardine(HttpClientBuilder builder) {
        super(builder);
    }

    /**
     * had to be overriden, because parent method did not close connection after execution
     */
    @Override
    protected <T> T execute(HttpRequestBase request, ResponseHandler<T> responseHandler) throws IOException {
        notNull(request, "request");
        try {
            return super.execute(request, responseHandler);
        } finally {
            request.releaseConnection();
        }
    }

    /**
     * The method body is retrieved from {@link SardineImpl#get(String, Map)} and extended about the response handler
     * to be able to handle responses arbitrarily.
     *
     * @param url             Path to the resource including protocol and hostname
     * @param headers         Additional HTTP headers to add to the request
     * @param responseHandler Arbitrary response handler to manipulate with responses
     * @return Data stream to read from
     * @throws IOException I/O error or HTTP response validation failure
     */
    public <T> ContentLengthInputStream get(final String url, final List<Header> headers,
                                            final ResponseHandler<T> responseHandler) throws IOException {

        final HttpGet get = new HttpGet(url);
        for (Header header : headers) {
            get.addHeader(header);
        }
        // Must use #execute without handler, otherwise the entity is consumed
        // already after the handler exits.
        final HttpResponse response = this.execute(get);
        try {
            responseHandler.handleResponse(response);
            // Will abort the read when closed before EOF.
            return new ContentLengthInputStream(new HttpMethodReleaseInputStream(response), response.getEntity().getContentLength());
        } catch (IOException ex) {
            get.abort();
            throw ex;
        }
    }
}
