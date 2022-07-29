/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.project;

import org.testng.annotations.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class UserTest {

    @Test
    public void testDeserialization() throws Exception {
        final User user = readObjectFromResource("/project/project-user.json", User.class);

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
    public void testToStringFormat() throws Exception {
        final User user = readObjectFromResource("/project/project-user.json", User.class);

        assertThat(user.toString(), matchesPattern(User.class.getSimpleName() + "\\[.*\\]"));
    }

}