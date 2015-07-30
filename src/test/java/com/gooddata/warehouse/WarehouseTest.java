package com.gooddata.warehouse;

import static com.gooddata.JsonMatchers.serializesToJson;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gooddata.project.Environment;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class WarehouseTest {

    public static final String TITLE = "Test";
    public static final String TOKEN = "{Token}";
    public static final String DESCRIPTION = "Storage";
    private static final String ENVIRONMENT = "TESTING";
    public static final DateTime CREATED = new DateTime(2014, 5, 5, 8, 27, 33, DateTimeZone.UTC);
    public static final DateTime UPDATED = new DateTime(2014, 5, 5, 8, 27, 34, DateTimeZone.UTC);
    public static final String CREATED_BY = "/gdc/account/profile/createdBy";
    public static final String UPDATED_BY = "/gdc/account/profile/updatedBy";
    public static final String STATUS = "ENABLED";
    public static final Map<String, String> LINKS = new LinkedHashMap<String, String>() {{
        put("self", "/gdc/datawarehouse/instances/instanceId");
        put("parent", "/gdc/datawarehouse/instances");
        put("users", "/gdc/datawarehouse/instances/instanceId/users");
        put("jdbc", "/gdc/datawarehouse/instances/instanceId/jdbc");
    }};

    @Test
    public void testSerializationForInstanceCreation() throws Exception {
        final Warehouse warehouse = new Warehouse("New ADS", "Your-ADS-Token", "ADS Description");
        warehouse.setEnvironment(Environment.TESTING);
        assertThat(warehouse, serializesToJson("/warehouse/warehouse-create.json"));
    }

    @Test
    public void testSerialization() throws Exception {
        final Warehouse warehouse = new Warehouse(TITLE, TOKEN, DESCRIPTION, CREATED, UPDATED, CREATED_BY, UPDATED_BY, STATUS, ENVIRONMENT, LINKS);
        assertThat(warehouse, serializesToJson("/warehouse/warehouse.json"));
    }

    @Test
    public void testDeserialization() throws Exception {
        final InputStream stream = getClass().getResourceAsStream("/warehouse/warehouse.json");
        final Warehouse warehouse = new ObjectMapper().readValue(stream, Warehouse.class);

        assertThat(warehouse.getTitle(), is(TITLE));
        assertThat(warehouse.getDescription(), is(DESCRIPTION));
        assertThat(warehouse.getAuthorizationToken(), is(TOKEN));
        assertThat(warehouse.getEnvironment(), is(ENVIRONMENT));
        assertThat(warehouse.getCreatedBy(), is(CREATED_BY));
        assertThat(warehouse.getUpdatedBy(), is(UPDATED_BY));
        assertThat(warehouse.getCreated(), is(CREATED));
        assertThat(warehouse.getUpdated(), is(UPDATED));
        assertThat(warehouse.getStatus(), is(STATUS));
        assertThat(warehouse.getLinks(), is(LINKS));
    }
}