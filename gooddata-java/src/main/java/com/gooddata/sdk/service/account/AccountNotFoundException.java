/*
 * (C) 2025 GoodData Corporation.
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

    public AccountNotFoundException(final String message,
                                    final String accountUri) {
        super(message + " failed on " + accountUri);
        this.accountUri = accountUri;
    }

    public String getAccountUri() {
        return accountUri;
    }
}

