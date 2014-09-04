package com.gooddata.warehouse;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.InputStream;

public class WarehousesTest {

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/warehouse/warehouses.json");
        final Warehouses warehouses = new ObjectMapper().readValue(stream, Warehouses.class);

        assertNotNull(warehouses.getItems());
        assertThat(warehouses.getItems(), hasSize(2));
        assertThat(warehouses.getItems().get(0).getTitle(), is("Storage"));
    }
}