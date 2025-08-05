/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.*;

import org.junit.jupiter.api.Test;

import java.util.Collections;

public class WarehousesTest {

    private final Warehouses warehouses = readObjectFromResource("/warehouse/warehouses.json", Warehouses.class);

    private final Warehouses empty = new Warehouses(Collections.emptyList(), null);

    @Test
    public void testDeserialization() throws Exception {
        assertThat(warehouses, notNullValue());
        assertThat(warehouses.getPageItems(), hasSize(2));
        assertThat(warehouses.getPageItems().get(0).getTitle(), is("Storage"));
    }

    @Test
    public void testSerialization() throws Exception {
        assertThat(warehouses, jsonEquals(resource("warehouse/warehouses.json")));
    }

    @Test
    public void shouldSerializeEmpty() throws Exception {
        assertThat(empty, jsonEquals(resource("warehouse/warehouses-empty.json")));
    }

    @Test
    public void shouldDeserializeEmpty() throws Exception {
        final Warehouses result = readObjectFromResource("/warehouse/warehouses-empty.json", Warehouses.class);
        assertThat(result.getPageItems(), hasSize(0));
        assertThat(result.getPaging(), is(notNullValue()));
    }
}