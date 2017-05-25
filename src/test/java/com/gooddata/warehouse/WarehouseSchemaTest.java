/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class WarehouseSchemaTest {

    private static final String NAME = "default";
    private static final String DESCRIPTION = "Default schema for new ADS instance";
    private static final String SELF_LINK = "/gdc/datawarehouse/instances/instanceId/schemas/default";
    private static final String INSTANCE = "/gdc/datawarehouse/instances/instanceId";
    private static final String PARENT = "/gdc/datawarehouse/instances/warehouseId/schemas";
    private static final Map<String, String> LINKS = new LinkedHashMap<String, String>() {{
        put("self", SELF_LINK);
        put("instance", INSTANCE);
        put("parent", PARENT);
    }};

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/warehouse/schema.json");
        final WarehouseSchema warehouseSchema = new ObjectMapper().readValue(stream, WarehouseSchema.class);

        assertThat(warehouseSchema.getName(), is(NAME));
        assertThat(warehouseSchema.getDescription(), is(DESCRIPTION));
        assertThat(warehouseSchema.getLinks(), is(equalTo(LINKS)));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/warehouse/schema.json");
        final WarehouseSchema warehouseSchema = new ObjectMapper().readValue(stream, WarehouseSchema.class);

        assertThat(warehouseSchema.toString(), matchesPattern(WarehouseSchema.class.getSimpleName() + "\\[.*\\]"));
    }
}
