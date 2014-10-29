package com.gooddata.warehouse;

import com.gooddata.AbstractGoodDataIT;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collection;

import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static net.jadler.Jadler.verifyThatRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;


public class WarehouseServiceIT extends AbstractGoodDataIT {

    private static final String TITLE = "Test";
    private static final String TASK_POLL = "/warehouse/warehouseTask-poll.json";
    private static final String TASK_DONE = "/warehouse/warehouseTask-finished.json";
    private static final String WAREHOUSE_ID = "instanceId";
    private static final String WAREHOUSE = "/warehouse/warehouse.json";

    private static final String WAREHOUSE_URI = Warehouse.TEMPLATE.expand(WAREHOUSE_ID).toString();

    private WarehouseTask pollingTask;
    private WarehouseTask finishedTask;
    private Warehouse warehouse;

    @BeforeClass
    public void setUp() throws Exception {
        pollingTask = MAPPER.readValue(readResource(TASK_POLL), WarehouseTask.class);
        finishedTask = MAPPER.readValue(readResource(TASK_DONE), WarehouseTask.class);
        warehouse = MAPPER.readValue(readResource(WAREHOUSE), Warehouse.class);
    }

    @Test
    public void shouldCreateWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollLink())
            .respond()
                .withStatus(202)
                .thenRespond()
                .withBody(readResource(TASK_DONE))
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseLink())
            .respond()
                .withBody(readResource(WAREHOUSE))
                .withStatus(200);

        final Warehouse created = gd.getWarehouseService().createWarehouse(new Warehouse(TITLE, "{Token}", "Storage")).get();
        assertThat(created, notNullValue());
        assertThat(created.getTitle(), is(TITLE));
        assertThat(created.getJdbcConnectionString(), is("jdbc:gdc:datawarehouse://localhost:" + port() + WAREHOUSE_URI));
    }

    @Test
    public void shouldListWarehouses() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readResource("/warehouse/warehouses.json"))
                .withStatus(200);

        final Collection<Warehouse> list = gd.getWarehouseService().listWarehouses();
        assertThat(list, notNullValue());
        assertThat(list, hasSize(2));
    }

    @Test
    public void shouldRemoveWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_URI)
            .respond()
                .withStatus(204);

        gd.getWarehouseService().removeWarehouse(warehouse);
    }

    @Test
    public void shouldGetWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouse.getUri())
                .respond()
                .withBody(readResource(WAREHOUSE))
                .withStatus(200);

        final Warehouse warehouse = gd.getWarehouseService().getWarehouseById(WAREHOUSE_ID);
        assertThat(warehouse, notNullValue());
        assertThat(warehouse.getTitle(), is(TITLE));
    }

    @Test
    public void shouldUpdateWarehouse() throws Exception {

        final String updatedTitle = "UPDATED_TITLE";

        final Warehouse toUpdate = MAPPER.readValue(readResource(WAREHOUSE), Warehouse.class);
        toUpdate.setTitle(updatedTitle);

        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(warehouse.getUri())
                .respond()
                .withStatus(204);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouse.getUri())
                .respond()
                .withBody(MAPPER.writeValueAsString(toUpdate))
                .withStatus(200);

        final Warehouse updated = gd.getWarehouseService().updateWarehouse(toUpdate);
        assertThat(updated, notNullValue());
        assertThat(updated.getTitle(), is(updatedTitle));

        verifyThatRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(warehouse.getUri())
                .havingBody(new BaseMatcher<String>() {
                    @Override
                    public boolean matches(Object o) {
                        try {
                            Warehouse instance = MAPPER.readValue((String) o, Warehouse.class);
                            return updatedTitle.equals(instance.getTitle());
                        } catch (IOException e) {
                            return false;
                        }
                    }


                    @Override
                    public void describeTo(Description description) {
                        description.appendText("Warehouse not changed");
                    }
                })
                .receivedOnce();
    }
}
