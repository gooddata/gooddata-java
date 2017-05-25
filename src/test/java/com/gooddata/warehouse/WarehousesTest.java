/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import static com.gooddata.JsonMatchers.serializesToJson;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.*;

import org.testng.annotations.Test;

import java.util.Collections;

public class WarehousesTest {

    private final Warehouses warehouses = readObjectFromResource("/warehouse/warehouses.json", Warehouses.class);

    private final Warehouses empty = new Warehouses(Collections.emptyList(), null);

    @Test
    public void testDeserialization() throws Exception {
        assertThat(warehouses, notNullValue());
        assertThat(warehouses, hasSize(2));
        assertThat(warehouses.get(0).getTitle(), is("Storage"));
    }

    @Test
    public void testSerialization() throws Exception {
        assertThat(warehouses, serializesToJson("/warehouse/warehouses.json"));
    }

    @Test
    public void shouldSerializeEmpty() throws Exception {
        assertThat(empty, serializesToJson("/warehouse/warehouses-empty.json"));
    }

    @Test
    public void shouldDeserializeEmpty() throws Exception {
        final Warehouses result = readObjectFromResource("/warehouse/warehouses-empty.json", Warehouses.class);
        assertThat(result, hasSize(0));
        assertThat(result.getPaging(), is(notNullValue()));
    }
}