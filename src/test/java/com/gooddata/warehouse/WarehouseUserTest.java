/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
        final WarehouseUser user =  readObjectFromResource("/warehouse/user.json", WarehouseUser.class);

        assertThat(user.toString(), matchesPattern(WarehouseUser.class.getSimpleName() + "\\[.*\\]"));
    }
}
