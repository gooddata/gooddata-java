package com.gooddata.account;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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

        account = accountService.createAccount(newAccount, getProperty("domain"));

        assertThat(account, is(notNullValue()));
        assertThat(account.getId(), is(notNullValue()));
        assertThat(account.getLogin(), is(LOGIN));
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "createAccount")
    public void getAccount() {
        final Account foundAccount = accountService.getAccount(this.account.getId());

        assertThat(foundAccount, is(notNullValue()));
        assertThat(foundAccount.getId(), is(notNullValue()));
        assertThat(foundAccount.getId(), is(account.getId()));
        assertThat(foundAccount.getLogin(), is(LOGIN));
        assertThat(foundAccount.getLogin(), is(account.getLogin()));
    }

    @Test(groups = "isolated_domain", dependsOnMethods = "getAccount")
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
