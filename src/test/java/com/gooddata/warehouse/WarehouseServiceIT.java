package com.gooddata.warehouse;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.GoodDataException;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static net.jadler.Jadler.onRequest;
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
    private static final String WAREHOUSE_USER = "/warehouse/user.json";

    private static final String WAREHOUSE_URI = Warehouse.TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String WAREHOUSE_USER_URI = WarehouseUsers.TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String REMOVE_USER_TASK_DONE = "/warehouse/removeUserTask-finished.json";

    private static final String CONNECTION_URL = "CONNECTION_URL";

    private WarehouseTask pollingTask;
    private WarehouseTask finishedTask;
    private Warehouse warehouse;

    @BeforeClass
    public void setUp() throws Exception {
        pollingTask = MAPPER.readValue(readFromResource(TASK_POLL), WarehouseTask.class);
        finishedTask = MAPPER.readValue(readFromResource(TASK_DONE), WarehouseTask.class);
        warehouse = MAPPER.readValue(readFromResource(WAREHOUSE), Warehouse.class);
    }

    @Test
    public void shouldCreateWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
            .respond()
                .withStatus(202)
                .thenRespond()
                .withBody(readFromResource(TASK_DONE))
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseUri())
            .respond()
                .withBody(readFromResource(WAREHOUSE))
                .withStatus(200);

        final Warehouse created = gd.getWarehouseService().createWarehouse(new Warehouse(TITLE, "{Token}", "Storage")).get();
        assertThat(created, notNullValue());
        assertThat(created.getTitle(), is(TITLE));
        assertThat(created.getConnectionUrl(), is(CONNECTION_URL));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailToCreateWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
            .respond()
                .withStatus(400)
        ;
        gd.getWarehouseService().createWarehouse(new Warehouse(TITLE, "{Token}", "Storage")).get();
    }

    @Test
    public void shouldListWarehouses() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readFromResource("/warehouse/warehouses.json"))
                .withStatus(200);

        final PageableList<Warehouse> list = gd.getWarehouseService().listWarehouses();
        assertThat(list, notNullValue());
        assertThat(list, hasSize(2));
        assertThat(list.get(0).getConnectionUrl(), notNullValue());
        assertThat(list.get(1).getConnectionUrl(), notNullValue());
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
                .withBody(readFromResource(WAREHOUSE))
                .withStatus(200);

        final Warehouse warehouse = gd.getWarehouseService().getWarehouseById(WAREHOUSE_ID);
        assertThat(warehouse, notNullValue());
        assertThat(warehouse.getTitle(), is(TITLE));
        assertThat(warehouse.getConnectionUrl(), notNullValue());
    }

    @Test
    public void shouldUpdateWarehouse() throws Exception {

        final String updatedTitle = "UPDATED_TITLE";

        final Warehouse toUpdate = MAPPER.readValue(readFromResource(WAREHOUSE), Warehouse.class);
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

    @Test
    public void shouldPageUsersList() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/datawarehouse/instances/instanceId/users")
                .havingParameterEqualTo("limit", "2")
            .respond()
                .withBody(readFromResource("/warehouse/users.json"));

        final PageableList<WarehouseUser> users = gd.getWarehouseService().listWarehouseUsers(warehouse, new PageRequest(2));
        assertThat(users, Matchers.hasSize(2));
    }

    @Test
    public void shouldAddUserToWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(WAREHOUSE_USER_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(202)
                .thenRespond()
                .withBody(readFromResource(TASK_DONE))
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseUserUri())
                .respond()
                .withBody(readFromResource(WAREHOUSE_USER))
                .withStatus(200);

        final WarehouseUser created = gd.getWarehouseService().addUserToWarehouse(
                warehouse, new WarehouseUser("role", "profile", null)).get();
        assertThat(created, notNullValue());
        assertThat(created.getRole(), is("admin"));
        assertThat(created.getLogin(), is("foo@bar.com"));
        assertThat(created.getProfile(), is("/gdc/account/profile/{profile-id}"));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailToAddUserToWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(WAREHOUSE_USER_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().addUserToWarehouse(warehouse, new WarehouseUser("role", "profile", null)).get();
    }

    @Test
    public void shouldRemoveUserFromWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_USER_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(202)
                .thenRespond()
                .withBody(readFromResource(REMOVE_USER_TASK_DONE))
                .withStatus(201);

        gd.getWarehouseService()
          .removeUserFromWarehouse(new WarehouseUser("role", "profile", "login", Collections.singletonMap("self", WAREHOUSE_USER_URI)))
          .get();
    }

    @Test(expectedExceptions = WarehouseUserNotFoundException.class)
    public void shouldFailToFindUserForRemovalFromWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_USER_URI)
                .respond()
                .withStatus(404);

        gd.getWarehouseService().removeUserFromWarehouse(new WarehouseUser("role", "profile", "login", Collections.singletonMap("self", WAREHOUSE_USER_URI)));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void shouldFailWhenRemoveUserTaskFails() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_USER_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService()
          .removeUserFromWarehouse(new WarehouseUser("role", "profile", "login", Collections.singletonMap("self", WAREHOUSE_USER_URI)))
          .get();
    }

}
