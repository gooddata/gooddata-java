package com.gooddata.account;

import com.gooddata.AbstractGoodDataAT;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Account acceptance tests.
 */
@Test(groups = "account")
public class AccountServiceAT extends AbstractGoodDataAT {

    @Test
    public void login() throws Exception {
        final AccountService accountService = gd.getAccountService();
        final Account current = accountService.getCurrent();
        assertThat(current.getId(), is(notNullValue()));
    }

}
