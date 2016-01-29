/*
 * Copyright (C) 2007-2015, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import static com.gooddata.util.Validate.notNull;

import com.github.sardine.impl.SardineImpl;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;

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
        try {
            notNull(request,"request must not be null!");
            return super.execute(request, responseHandler);
        } finally {
            request.releaseConnection();
        }
    }
}
