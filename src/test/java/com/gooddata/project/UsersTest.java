/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.project;

import com.gooddata.account.Account;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class UsersTest {

    @Test
    public void testDeserialization() throws Exception {
        final Users users = readObjectFromResource("/project/project-users.json", Users.class);

        assertThat(users, notNullValue());
        assertThat(users, hasSize(1));
        assertThat(users.getNextPage(), nullValue());

        final User user = users.get(0);
        assertThat(user, notNullValue());
        assertThat(user.getEmail(), is("ateam+ads-testing@gooddata.com"));
        assertThat(user.getFirstName(), is("ateam"));
        assertThat(user.getUserRoles(), hasItem("/gdc/projects/PROJECT_ID/roles/ROLE1"));
        assertThat(user.getPhoneNumber(), is("123456789"));
        assertThat(user.getStatus(), is("ENABLED"));
        assertThat(user.getLastName(), is("ads-testing"));
        assertThat(user.getLogin(), is("ateam+ads-testing@gooddata.com"));
    }

    @Test
    public void testSerialize() throws Exception {
        final Account account = mock(Account.class);
        doReturn("/gdc/account/profile/USER_ID").when(account).getUri();

        final Role role = mock(Role.class);
        doReturn("/gdc/projects/PROJECT_ID/roles/ROLE1").when(role).getUri();

        final Users users = new Users(new User(account, role));

        assertThat(users, jsonEquals(resource("project/addUsersToProject.json")));
    }
}