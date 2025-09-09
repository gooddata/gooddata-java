/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import com.gooddata.sdk.model.account.Account;
import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

public class WarehouseUserTest {

    public static final String ROLE = "admin";
    public static final String PROFILE = "/gdc/account/profile/{profile-id}";
    public static final String LOGIN = "foo@bar.com";
    public static final String SELF_LINK = "/gdc/datawarehouse/instances/{instance-id}/users/{profile-id}";
    public static final Map<String, String> LINKS = new LinkedHashMap<String, String>() {{
        put("self", SELF_LINK);
        put("parent", "/gdc/datawarehouse/instances/{instance-id}/users");
    }};

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateWithProfileIdWithNullRole() throws Exception {
        WarehouseUser.createWithProfileUri(PROFILE, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateWithProfileWithNullRole() throws Exception {
        final Account account = readObjectFromResource("/account/account.json", Account.class);
        WarehouseUser.createWithProfile(account, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testCreateWithLoginWithNullRole() throws Exception {
        WarehouseUser.createWithlogin(LOGIN, null);
    }

    @Test
    public void testCreateWithProfileId() throws Exception {
        final WarehouseUser user = WarehouseUser.createWithProfileUri(PROFILE, WarehouseUserRole.ADMIN);
        assertThat(user.getRole(), is(WarehouseUserRole.ADMIN.getRoleName()));
        assertThat(user.getProfile(), is(PROFILE));
        assertThat(user.getLogin(), nullValue());
    }

    @Test
    public void testCreateWithProfile() throws Exception {
        final Account account = readObjectFromResource("/account/account.json", Account.class);

        final WarehouseUser user = WarehouseUser.createWithProfile(account, WarehouseUserRole.ADMIN);
        assertThat(user.getRole(), is(WarehouseUserRole.ADMIN.getRoleName()));
        assertThat(user.getProfile(), is(account.getId()));
        assertThat(user.getLogin(), nullValue());
    }

    @Test
    public void testCreateWithLogin() throws Exception {
        final WarehouseUser user = WarehouseUser.createWithlogin(LOGIN, WarehouseUserRole.ADMIN);
        assertThat(user.getRole(), is(WarehouseUserRole.ADMIN.getRoleName()));
        assertThat(user.getProfile(), nullValue());
        assertThat(user.getLogin(), is(LOGIN));
    }

    @Test
    public void testSerializationWithProfile() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, PROFILE, null);
        assertThat(user, jsonEquals(resource("warehouse/user-createWithProfile.json")));
    }

    @Test
    public void testSerializationWithLogin() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, null, LOGIN);
        assertThat(user, jsonEquals(resource("warehouse/user-createWithLogin.json")));
    }

    @Test
    public void testCompleteSerialization() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, PROFILE, LOGIN, LINKS);
        assertThat(user, jsonEquals(resource("warehouse/user.json")));
    }

    @Test
    public void testDeserialization() throws Exception {
        final WarehouseUser user = readObjectFromResource("/warehouse/user.json", WarehouseUser.class);

        assertThat(user.getRole(), is(ROLE));
        assertThat(user.getProfile(), is(PROFILE));
        assertThat(user.getLogin(), is(LOGIN));
        assertThat(user.getUri(), is(SELF_LINK));
        assertThat(user.getLinks(), is(LINKS));
    }

    @Test
    public void testGetId() throws Exception {
        final WarehouseUser user = new WarehouseUser(ROLE, PROFILE, LOGIN, LINKS);
        assertThat(user.getId(), is("{profile-id}"));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final WarehouseUser user = readObjectFromResource("/warehouse/user.json", WarehouseUser.class);

        assertThat(user.toString(), matchesPattern(WarehouseUser.class.getSimpleName() + "\\[.*\\]"));
    }
}

