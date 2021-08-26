/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.account;

import com.gooddata.sdk.common.collections.PageDeserializer;
import com.gooddata.sdk.common.collections.Paging;

import java.util.List;
import java.util.Map;

class AccountsDeserializer extends PageDeserializer<Accounts, Account> {

    protected AccountsDeserializer() {
        super(Account.class);
    }

    @Override
    protected Accounts createPage(final List<Account> items, final Paging paging, final Map<String, String> links) {
        return new Accounts(items, paging, links);
    }
}
