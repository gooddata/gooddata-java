/**
 * Copyright (C) 2004-2016, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import org.testng.annotations.Test;

import java.util.Collections;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;

public class WarehouseUsersTest {

    private final WarehouseUsers users = readObjectFromResource("/warehouse/users.json", WarehouseUsers.class);

    private final WarehouseUsers empty = new WarehouseUsers(Collections.<WarehouseUser>emptyList(), null);

    @Test
    public void testDeserialization() throws Exception {
        assertThat(users, notNullValue());
        assertThat(users, hasSize(2));
        assertThat(users.get(0).getProfile(), startsWith("/gdc/account/profile/{profile-id"));
    }

    @Test
    public void shouldDeserializeEmpty() throws Exception {
        final WarehouseUsers result = readObjectFromResource("/warehouse/users-empty.json", WarehouseUsers.class);
        assertThat(result, hasSize(0));
        assertThat(result.getPaging(), is(notNullValue()));
    }

    @Test
    public void testSerialization() throws Exception {
        assertThat(users, serializesToJson("/warehouse/users.json"));
    }

    @Test
    public void shouldSerializeEmpty() throws Exception {
        assertThat(empty, serializesToJson("/warehouse/users-empty.json"));
    }
}