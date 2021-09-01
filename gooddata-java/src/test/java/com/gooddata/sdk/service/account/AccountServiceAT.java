/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.account;

import com.gooddata.sdk.model.account.SeparatorSettings;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.model.account.Account;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.gooddata.sdk.model.account.Account.AuthenticationMode.PASSWORD;
import static com.gooddata.sdk.model.account.Account.AuthenticationMode.SSO;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Account acceptance tests.
 */
@Test(groups = "account")
public class AccountServiceAT extends AbstractGoodDataAT {

    private static final String LOGIN = "john.smith." + UUID.randomUUID() + "@gooddata.com";

    private final AccountService accountService = gd.getAccountService();

    private Account account;

    @Test
    public void login() throws Exception {
        final Account current = accountService.getCurrent();
        assertThat(current.getId(), is(notNullValue()));
    }

    @Test(groups = "isolated_domain")
    public void createAccount() {
        final Account newAccount = new Account(LOGIN, "w4yYxSQpAbaODA64", "FistName", "LastName");
        newAccount.setAuthenticationModes(asList(SSO.toString(), PASSWORD.toString()));
        account = accountService.createAccount(newAccount, getProperty("domain"));

        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(notNullValue()));
        assertThat(account.getLogin(), is(LOGIN));
        assertThat(account.getAuthenticationModes(), containsInAnyOrder(SSO.toString(), PASSWORD.toString()));
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "createAccount")
    public void getAccount() {
        final Account foundAccount = accountService.getAccountById(this.account.getId());

        assertThat(foundAccount, is(notNullValue()));
        assertThat(foundAccount.getId(), is(notNullValue()));
        assertThat(foundAccount.getId(), is(account.getId()));
        assertThat(foundAccount.getLogin(), is(LOGIN));
        assertThat(foundAccount.getLogin(), is(account.getLogin()));
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "createAccount")
    public void getAccountByLogin() {
        final Account foundAccount = accountService.getAccountByLogin(LOGIN, getProperty("domain"));

        assertThat(foundAccount, is(notNullValue()));
        assertThat(foundAccount.getId(), is(notNullValue()));
        assertThat(foundAccount.getId(), is(account.getId()));
        assertThat(foundAccount.getLogin(), is(LOGIN));
        assertThat(foundAccount.getLogin(), is(account.getLogin()));
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "getAccount")
    public void getSeparatorSettings() {
        final SeparatorSettings separators = accountService.getSeparatorSettings(account);

        assertThat(separators, notNullValue());
        assertThat(separators.getThousand(), notNullValue());
        assertThat(separators.getDecimal(), notNullValue());
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "getSeparatorSettings")
    public void updateAccount() {
        final String newName = "Petra";
        account.setFirstName(newName);

        accountService.updateAccount(account);

        Account accountByUri = accountService.getAccountByUri(account.getUri());

        assertThat(accountByUri.getFirstName(), is(newName));
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "updateAccount")
    public void removeAccount() {
        accountService.removeAccount(account);
        account = null;
    }

    @AfterClass
    public void tearDown() {
        if (account != null) {
            try {
                accountService.removeAccount(account);
            } catch (Exception ignored) {}
        }
    }

}
