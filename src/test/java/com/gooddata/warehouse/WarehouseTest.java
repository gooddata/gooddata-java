package com.gooddata.warehouse;


import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

public class WarehouseTest {

    @Test
    public void testSerialization() throws Exception {
        final Warehouse warehouse = new Warehouse("New ADS", "Your-ADS-Token", "ADS Description");
        assertThat(warehouse, serializesToJson("/warehouse/warehouse-create.json"));
    }

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/warehouse/warehouse.json");
        final Warehouse warehouse = new ObjectMapper().readValue(stream, Warehouse.class);

        assertThat(warehouse.getTitle(), is("Test"));
        assertThat(warehouse.getDescription(), is("Storage"));
        assertThat(warehouse.getAuthorizationToken(), is("{Token}"));
        assertThat(warehouse.getCreatedBy(), is("/gdc/account/profile/createdBy"));
        assertThat(warehouse.getUpdatedBy(), is("/gdc/account/profile/updatedBy"));
        assertThat(warehouse.getCreated(), is("2014-05-05T08:27:33.000Z"));
        assertThat(warehouse.getUpdated(), is("2014-05-05T08:27:34.000Z"));
        assertThat(warehouse.getAuthorizationToken(), is("{Token}"));
    }
}