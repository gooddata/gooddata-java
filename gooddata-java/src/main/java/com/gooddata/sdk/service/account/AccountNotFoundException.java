/*
 * (C) 2021 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.account;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;

/**
 * Account doesn't exist.
 */
public class AccountNotFoundException extends GoodDataException {

    private final String accountUri;

    public AccountNotFoundException(String uri, GoodDataRestException cause) {
        super("Account " + uri + " was not found", cause);
        this.accountUri = uri;
    }

    public String getAccountUri() {
        return accountUri;
    }
}
