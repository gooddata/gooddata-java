/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.warehouse;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.collections.CustomPageRequest;
import com.gooddata.sdk.common.collections.Page;
import com.gooddata.sdk.common.collections.PageBrowser;
import com.gooddata.sdk.model.warehouse.Warehouse;
import com.gooddata.sdk.model.warehouse.WarehouseSchema;
import com.gooddata.sdk.model.warehouse.WarehouseTask;
import com.gooddata.sdk.model.warehouse.WarehouseUser;
import com.gooddata.sdk.model.warehouse.Warehouses;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.verifyThatRequest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class WarehouseServiceIT extends AbstractGoodDataIT {

    private static final String TITLE = "Test";
    private static final String SCHEMA_NAME = "default";
    private static final String TASK_POLL = "/warehouse/warehouseTask-poll.json";
    private static final String TASK_DONE = "/warehouse/warehouseTask-finished.json";
    private static final String WAREHOUSE_ID = "instanceId";
    private static final String WAREHOUSE = "/warehouse/warehouse.json";
    private static final String WAREHOUSE_USER = "/warehouse/user.json";
    private static final String WAREHOUSE_SCHEMA = "/warehouse/schema.json";

    private static final String WAREHOUSE_URI = WarehouseService.WAREHOUSE_TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String WAREHOUSE_USER_URI = WarehouseService.USERS_TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String REMOVE_USER_TASK_DONE = "/warehouse/removeUserTask-finished.json";

    private static final String CONNECTION_URL = "CONNECTION_URL";

    private WarehouseTask pollingTask;
    private WarehouseTask finishedTask;
    private Warehouse warehouse;
    private WarehouseSchema warehouseSchema;

    @BeforeEach
    public void setUp() throws Exception {
        pollingTask = readObjectFromResource(TASK_POLL, WarehouseTask.class);
        finishedTask = readObjectFromResource(TASK_DONE, WarehouseTask.class);
        warehouse = readObjectFromResource(WAREHOUSE, Warehouse.class);
        warehouseSchema = readObjectFromResource(WAREHOUSE_SCHEMA, WarehouseSchema.class);
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

    @Test
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
        assertThrows(GoodDataException.class, () -> {
            gd.getWarehouseService().createWarehouse(new Warehouse(TITLE, "{Token}", "Storage")).get();
        });
    }

    @Test
    public void shouldListWarehouses() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(Warehouses.URI)
            .respond()
                .withBody(readFromResource("/warehouse/warehouses.json"))
                .withStatus(200);

        final Page<Warehouse> list = gd.getWarehouseService().listWarehouses();
        assertThat(list, notNullValue());
        final List<Warehouse> pageItems = list.getPageItems();
        assertThat(pageItems, hasSize(2));
        assertThat(pageItems.get(0).getConnectionUrl(), notNullValue());
        assertThat(pageItems.get(1).getConnectionUrl(), notNullValue());
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

        final Warehouse toUpdate = readObjectFromResource(WAREHOUSE, Warehouse.class);
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
                .withBody(OBJECT_MAPPER.writeValueAsString(toUpdate))
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
                            Warehouse instance = OBJECT_MAPPER.readValue((String) o, Warehouse.class);
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
                .havingParameterEqualTo("limit", "100")
                .respond()
                .withBody(readFromResource("/warehouse/users-with-next-page-link.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/datawarehouse/instances/instanceId/users")
                .havingParameterEqualTo("offset", "profile-id-next")
                .respond()
                .withBody(readFromResource("/warehouse/users.json"));

        final PageBrowser<WarehouseUser> users = gd.getWarehouseService().listWarehouseUsers(warehouse);
        assertThat(users.getPageItems().size(), is(2));
        assertThat(users.hasNextPage(), is(true));
        assertThat((int) users.allItemsStream().count(), is(4));
    }


    @Test
    public void shouldPageUsersListWithStartPage() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/datawarehouse/instances/instanceId/users")
                .havingParameterEqualTo("limit", "2")
                .respond()
                .withBody(readFromResource("/warehouse/users.json"));

        final Page<WarehouseUser> users = gd.getWarehouseService().listWarehouseUsers(warehouse, new CustomPageRequest(2));
        assertThat(users.getPageItems(), Matchers.hasSize(2));
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

    @Test
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
    
        assertThrows(GoodDataException.class, () -> {
            gd.getWarehouseService().addUserToWarehouse(warehouse, new WarehouseUser("role", "profile", null)).get();
        });
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

    @Test
    public void shouldFailToFindUserForRemovalFromWarehouse() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_USER_URI)
                .respond()
                .withStatus(404);

        assertThrows(WarehouseUserNotFoundException.class, () -> {
            gd.getWarehouseService().removeUserFromWarehouse(
                    new WarehouseUser("role", "profile", "login", Collections.singletonMap("self", WAREHOUSE_USER_URI)));
        });
    }

    @Test
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

        assertThrows(GoodDataException.class, () -> {
            gd.getWarehouseService()
              .removeUserFromWarehouse(new WarehouseUser("role", "profile", "login", Collections.singletonMap("self", WAREHOUSE_USER_URI)))
              .get();
        });
    }

    @Test
    public void shouldGetWarehouseSchemaByName() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouseSchema.getUri())
                .respond()
                .withBody(readFromResource(WAREHOUSE_SCHEMA))
                .withStatus(200);

        final WarehouseSchema result = gd.getWarehouseService().getWarehouseSchemaByName(warehouse, SCHEMA_NAME);
        assertThat(result, notNullValue());
        assertThat(result.getName(), is(SCHEMA_NAME));
    }

    @Test
    public void shouldGetDefaultWarehouseSchema() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouseSchema.getUri())
                .respond()
                .withBody(readFromResource(WAREHOUSE_SCHEMA))
                .withStatus(200);

        final WarehouseSchema result = gd.getWarehouseService().getDefaultWarehouseSchema(warehouse);
        assertThat(result, notNullValue());
        assertThat(result.getName(), is(SCHEMA_NAME));
    }

    @Test
    public void shouldGetWarehouseSchemaByUri() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouseSchema.getUri())
                .respond()
                .withBody(readFromResource(WAREHOUSE_SCHEMA))
                .withStatus(200);

        final WarehouseSchema result = gd.getWarehouseService().getWarehouseSchemaByUri(warehouseSchema.getUri());
        assertThat(result, notNullValue());
        assertThat(result.getName(), is(SCHEMA_NAME));
    }

    @Test
    public void shouldListWarehouseSchemas() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(WarehouseService.SCHEMAS_TEMPLATE.expand(warehouse.getId()).toString())
                .respond()
                .withBody(readFromResource("/warehouse/schemas.json"))
                .withStatus(200);

        final Page<WarehouseSchema> list = gd.getWarehouseService().listWarehouseSchemas(warehouse);
        assertThat(list, notNullValue());
        final List<WarehouseSchema> pageItems = list.getPageItems();
        assertThat(pageItems, hasSize(1));
        assertThat(pageItems.get(0).getName(), is(equalTo(SCHEMA_NAME)));
    }

    @Test
    public void shouldFailWarehouseSchemaByNameNotFound() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouseSchema.getUri())
                .respond()
                .withStatus(404);

        assertThrows(WarehouseSchemaNotFoundException.class, () -> {
            gd.getWarehouseService().getWarehouseSchemaByName(warehouse, SCHEMA_NAME);
        });
    }

}
