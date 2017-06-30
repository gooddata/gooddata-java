/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.account;

import org.testng.annotations.Test;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import java.util.Arrays;

public class AccountTest {

    private static final String MAIL = "fake@gooddata.com";
    private static final String FIRST_NAME = "Blah";
    private static final String LAST_NAME = "Muhehe";
    private static final String IP = "1.2.3.4/32";

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
        assertThat(account.getIpWhitelist(), contains(IP));
    }

    @Test
    public void testSerialization() {
        final Account account = new Account(FIRST_NAME, LAST_NAME, null);
        account.setIpWhitelist(Arrays.asList("1.2.3.4/32"));
        assertThat(account, jsonEquals(resource("account/account-input.json")));
    }

    @Test
    public void testSerializationOfCreateAccount() {
        final Account account = new Account(MAIL, "password", FIRST_NAME, LAST_NAME);
        assertThat(account, jsonEquals(resource("account/create-account.json")));
    }

    @Test
    public void testToStringFormat() {
        final Account account = new Account(FIRST_NAME, LAST_NAME, null);

        assertThat(account.toString(), matchesPattern(Account.class.getSimpleName() + "\\[.*\\]"));
    }

}
