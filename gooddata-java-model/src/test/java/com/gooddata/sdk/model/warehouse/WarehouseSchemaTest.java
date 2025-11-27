/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.model.warehouse;

import org.testng.annotations.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.text.MatchesPattern.matchesPattern;

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
        final WarehouseSchema warehouseSchema = readObjectFromResource("/warehouse/schema.json", WarehouseSchema.class);

        assertThat(warehouseSchema.getName(), is(NAME));
        assertThat(warehouseSchema.getDescription(), is(DESCRIPTION));
        assertThat(warehouseSchema.getLinks(), is(equalTo(LINKS)));
    }

    @Test
    public void testToStringFormat() throws Exception {
        final WarehouseSchema warehouseSchema = readObjectFromResource("/warehouse/schema.json", WarehouseSchema.class);

        assertThat(warehouseSchema.toString(), matchesPattern(WarehouseSchema.class.getSimpleName() + "\\[.*\\]"));
    }
}
