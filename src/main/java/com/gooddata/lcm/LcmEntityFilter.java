/*
 * Copyright (C) 2007-2018, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.lcm;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static java.util.Collections.singletonList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Specification of the filter on {@link LcmEntities}.
 */
public class LcmEntityFilter {

    private static final String DATA_PRODUCT = "dataProduct";
    private static final String SEGMENT = "segment";
    private static final String CLIENT = "client";

    private String dataProduct;
    private String segment;
    private String client;

    /**
     * Creates new (empty) filter.
     */
    public LcmEntityFilter() {
    }

    /**
     * Adds given data product to this filter.
     * @param dataProduct data product id - must not be empty.
     * @return this filter
     */
    public LcmEntityFilter withDataProduct(final String dataProduct) {
        if (isNotBlank(dataProduct)) {
            this.dataProduct = dataProduct;
        }
        return this;
    }

    /**
     * Adds given segment to this filter.
     * @param segment segment id - must not be empty.
     * @return this filter
     */
    public LcmEntityFilter withSegment(final String segment) {
        if (isNotBlank(segment)) {
            this.segment = segment;
        }
        return this;
    }

    /**
     * Adds given client to this filter.
     * @param client client id - must not be empty.
     * @return this filter
     */
    public LcmEntityFilter withClient(final String client) {
        if (isNotBlank(client)) {
            this.client = client;
        }
        return this;
    }

    public String getDataProduct() {
        return dataProduct;
    }

    public String getSegment() {
        return segment;
    }

    public String getClient() {
        return client;
    }

    /**
     * This filter in the form of query parameters map.
     * @return filter as query params map
     */
    public MultiValueMap<String, String> asQueryParams() {
        final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        if (dataProduct != null) {
            params.put(DATA_PRODUCT, singletonList(dataProduct));
        }
        if (segment != null) {
            params.put(SEGMENT, singletonList(segment));
        }
        if (client != null) {
            params.put(CLIENT, singletonList(client));
        }
        return params;
    }
}
