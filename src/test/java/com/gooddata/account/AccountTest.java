/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.account;

import org.testng.annotations.Test;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class AccountTest {

    private static final String MAIL = "fake@gooddata.com";
    private static final String FIRST_NAME = "Blah";
    private static final String LAST_NAME = "Muhehe";

    @SuppressWarnings("deprecation")
    @Test
    public void testDeserialize() throws Exception {
        final Account account = readObjectFromResource("/account/account.json", Account.class);
        assertThat(account, is(notNullValue()));

        assertThat(account.getFirstName(), is(FIRST_NAME));
        assertThat(account.getLastName(), is(LAST_NAME));
        assertThat(account.getId(), is("ID"));
        assertThat(account.getUri(), is("/gdc/account/profile/ID"));
        assertThat(account.getProjectsLink(), is("/gdc/account/profile/ID/projects"));
        assertThat(account.getProjectsUri(), is("/gdc/account/profile/ID/projects"));
    }

    @Test
    public void testSerialization() {
        final Account account = new Account(FIRST_NAME, LAST_NAME, null);
        assertThat(account, serializesToJson("/account/account-input.json"));
    }

    @Test
    public void testSerializationOfCreateAccount() {
        final Account account = new Account(MAIL, "password", FIRST_NAME, LAST_NAME);
        assertThat(account, serializesToJson("/account/create-account.json"));
    }

    @Test
    public void testToStringFormat() {
        final Account account = new Account(FIRST_NAME, LAST_NAME, null);

        assertThat(account.toString(), matchesPattern(Account.class.getSimpleName() + "\\[.*\\]"));
    }

}
