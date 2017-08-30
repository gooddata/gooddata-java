/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.collections.PageableList;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

/**
 * List of warehouse S3 credentials for a given warehouse
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@JsonTypeName("s3CredentialsList")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = WarehouseS3CredentialsListDeserializer.class)
@JsonSerialize(using = WarehouseS3CredentialsListSerializer.class)
public class WarehouseS3CredentialsList extends PageableList<WarehouseS3Credentials> {

    static final String ROOT_NODE = "s3CredentialsList";
    public static final String URI = Warehouse.URI + "/s3";
    public static final UriTemplate TEMPLATE = new UriTemplate(URI);

    public WarehouseS3CredentialsList(final List<WarehouseS3Credentials> items) {
        this(items, null);
    }

    public WarehouseS3CredentialsList(final List<WarehouseS3Credentials> items,
                               final Map<String, String> links) {
        super(items, null, links);
    }
}
