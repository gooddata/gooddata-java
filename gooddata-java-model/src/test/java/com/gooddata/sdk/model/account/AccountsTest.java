/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.account;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

public class AccountsTest {

    private static final String EMAIL = "example@company.com";
    private static final String FIRST_NAME = "Blah";
    private static final String LAST_NAME = "Muhehe";
    private static final String IP = "1.2.3.4/32";

    private final Accounts accounts = readObjectFromResource("/account/accounts.json", Accounts.class);


    @Test
    public void testDeserialization() throws Exception {
        assertThat(accounts, Matchers.notNullValue());
        assertThat(accounts.getPageItems(), hasSize(1));
        final Account account = accounts.getPageItems().get(0);
        assertThat(account.getFirstName(), is(FIRST_NAME));
        assertThat(account.getLastName(), is(LAST_NAME));
        assertThat(account.getId(), is("ID"));
        assertThat(account.getUri(), is("/gdc/account/profile/ID"));
        assertThat(account.getProjectsUri(), is("/gdc/account/profile/ID/projects"));
        assertThat(account.getIpWhitelist(), contains(IP));
        assertThat(account.getEmail(), is(EMAIL));
    }
}
