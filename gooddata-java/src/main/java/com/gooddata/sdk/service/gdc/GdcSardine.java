/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import static com.gooddata.sdk.common.util.Validate.notNull;

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
        notNull(request,"request");
        try {
            return super.execute(request, responseHandler);
        } finally {
            request.releaseConnection();
        }
    }
}
