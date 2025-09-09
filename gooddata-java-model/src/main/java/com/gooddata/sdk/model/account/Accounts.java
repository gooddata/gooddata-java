/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

/**
 * List of accounts. Deserialization only.
 */
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = Id.NAME)
@JsonTypeName(Accounts.ROOT_NODE)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = AccountsDeserializer.class)
public class Accounts extends Page<Account> {

    static final String ROOT_NODE = "accountSettings";


    Accounts(final List<Account> items, final Paging paging, final Map<String, String> links) {
        super(items, paging, links);
    }
}

