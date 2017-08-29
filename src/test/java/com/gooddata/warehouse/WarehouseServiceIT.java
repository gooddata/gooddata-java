/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.GoodDataException;
import com.gooddata.account.Account;
import com.gooddata.collections.MultiPageList;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.springframework.web.client.RestClientException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Collections;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.util.ResourceUtils.readStringFromResource;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.verifyThatRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;


public class WarehouseServiceIT extends AbstractGoodDataIT {

    private static final String TITLE = "Test";
    private static final String SCHEMA_NAME = "default";
    private static final String TASK_POLL = "/warehouse/warehouseTask-poll.json";
    private static final String TASK_DONE = "/warehouse/warehouseTask-finished.json";
    private static final String WAREHOUSE_ID = "instanceId";
    private static final String WAREHOUSE = "/warehouse/warehouse.json";
    private static final String WAREHOUSE_USER = "/warehouse/user.json";
    private static final String WAREHOUSE_SCHEMA = "/warehouse/schema.json";
    private static final String S3_CREDENTIALS_CREATE = "/warehouse/s3Credentials-create.json";
    private static final String S3_CREDENTIALS_UPDATE = "/warehouse/s3Credentials-update.json";
    private static final String REGION = "region";
    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String NEW_SECRET_KEY = "newSecretKey";

    private static final String WAREHOUSE_URI = Warehouse.TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String WAREHOUSE_USER_URI = WarehouseUsers.TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String WAREHOUSE_S3_CREDENTIALS_LIST_URI = WarehouseS3CredentialsList.TEMPLATE.expand(WAREHOUSE_ID).toString();
    private static final String WAREHOUSE_S3_CREDENTIALS_URI = WarehouseS3Credentials.TEMPLATE.expand(WAREHOUSE_ID, REGION, ACCESS_KEY).toString();
    private static final String REMOVE_USER_TASK_DONE = "/warehouse/removeUserTask-finished.json";
    private static final String UPDATED_BY_URI = Account.TEMPLATE.expand("updatedBy").toString();

    private static final String CONNECTION_URL = "CONNECTION_URL";
    private static final WarehouseS3Credentials.Links LINKS = new WarehouseS3Credentials.Links(
            WAREHOUSE_S3_CREDENTIALS_URI, WAREHOUSE_S3_CREDENTIALS_LIST_URI, WAREHOUSE_URI, UPDATED_BY_URI
    );

    private WarehouseTask pollingTask;
    private WarehouseTask finishedTask;
    private Warehouse warehouse;
    private WarehouseSchema warehouseSchema;
    private WarehouseS3Credentials s3Credentials;
    private WarehouseS3Credentials s3CredentialsWithLinks;

    @BeforeClass
    public void setUp() throws Exception {
        pollingTask = readObjectFromResource(TASK_POLL, WarehouseTask.class);
        finishedTask = readObjectFromResource(TASK_DONE, WarehouseTask.class);
        warehouse = readObjectFromResource(WAREHOUSE, Warehouse.class);
        warehouseSchema = readObjectFromResource(WAREHOUSE_SCHEMA, WarehouseSchema.class);
        s3Credentials = new WarehouseS3Credentials(REGION, ACCESS_KEY, SECRET_KEY);
        s3CredentialsWithLinks = new WarehouseS3Credentials(REGION, ACCESS_KEY, SECRET_KEY, DateTime.now(), LINKS);
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

        final MultiPageList<WarehouseUser> users = (MultiPageList<WarehouseUser>) gd.getWarehouseService().listWarehouseUsers(warehouse);
        assertThat(users.size(), is(2));
        assertThat(users.totalSize(), is(4));
        assertThat(users.collectAll().size(), is(4));
    }


    @Test
    public void shouldPageUsersListWithStartPage() throws Exception {
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
                .havingPathEqualTo(WarehouseSchemas.TEMPLATE.expand(warehouse.getId()).toString())
                .respond()
                .withBody(readFromResource("/warehouse/schemas.json"))
                .withStatus(200);

        final PageableList<WarehouseSchema> list = gd.getWarehouseService().listWarehouseSchemas(warehouse);
        assertThat(list, notNullValue());
        assertThat(list, hasSize(1));
        assertThat(list.get(0).getName(), is(equalTo(SCHEMA_NAME)));
    }

    @Test(expectedExceptions = WarehouseSchemaNotFoundException.class)
    public void shouldFailWarehouseSchemaByNameNotFound() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(warehouseSchema.getUri())
                .respond()
                .withStatus(404);

        final WarehouseSchema result = gd.getWarehouseService().getWarehouseSchemaByName(warehouse, SCHEMA_NAME);
        assertThat(result, notNullValue());
        assertThat(result.getName(), is(SCHEMA_NAME));
    }

    @Test
    public void shouldListWarehouseS3Credentials() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_LIST_URI)
                .respond()
                .withBody(readFromResource("/warehouse/s3CredentialsList.json"))
                .withStatus(200);

        final PageableList<WarehouseS3Credentials> credentials = gd.getWarehouseService().listWarehouseS3Credentials(warehouse);

        assertThat(credentials, notNullValue());
        assertThat(credentials, hasSize(2));
        assertThat(credentials.get(0).getRegion(), is(REGION));
        assertThat(credentials.get(0).getAccessKey(), is(ACCESS_KEY));
    }

    @Test(expectedExceptions = GoodDataException.class, expectedExceptionsMessageRegExp = ".*Unable to list Warehouse S3 credentials.*")
    public void shouldFailToListWarehouseS3Credentials_restClientError() {
        onRequest().respondUsing(request -> {
            throw new RestClientException("error");
        });

        gd.getWarehouseService().listWarehouseS3Credentials(warehouse);
    }

    @Test
    public void shouldGetSpecificWarehouseS3Credentials() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(WarehouseS3Credentials.TEMPLATE.expand(WAREHOUSE_ID, REGION, ACCESS_KEY).toString())
                .respond()
                .withBody(readFromResource("/warehouse/s3Credentials-get.json"))
                .withStatus(200);

        final WarehouseS3Credentials credentials = gd.getWarehouseService()
                .getWarehouseS3Credentials(warehouse, REGION, ACCESS_KEY);

        assertThat(credentials, notNullValue());
        assertThat(credentials.getRegion(), is(REGION));
        assertThat(credentials.getAccessKey(), is(ACCESS_KEY));
    }

    @Test(expectedExceptions = WarehouseS3CredentialsNotFoundException.class)
    public void shouldFailToGetSpecificWarehouseS3Credentials_credentialsNotFound() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(WarehouseS3Credentials.TEMPLATE.expand(warehouse.getId(), REGION, ACCESS_KEY).toString())
                .respond()
                .withStatus(404);

        gd.getWarehouseService().getWarehouseS3Credentials(warehouse, REGION, ACCESS_KEY);
    }

    @Test(expectedExceptions = GoodDataException.class, expectedExceptionsMessageRegExp = ".*Service Unavailable$")
    public void shouldFailToGetSpecificWarehouseS3Credentials_serviceNotFound() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(WarehouseS3Credentials.TEMPLATE.expand(warehouse.getId(), REGION, ACCESS_KEY).toString())
                .respond()
                .withStatus(503);

        gd.getWarehouseService().getWarehouseS3Credentials(warehouse, REGION, ACCESS_KEY);
    }

    @Test
    public void shouldAddS3Credentials() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_LIST_URI)
                .havingBody(jsonEquals(readStringFromResource(S3_CREDENTIALS_CREATE)))
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
                .havingPathEqualTo(finishedTask.getWarehouseS3CredentialsUri())
                .respond()
                .withBody(readFromResource("/warehouse/s3Credentials-get.json"))
                .withStatus(200);

        final WarehouseS3Credentials created = gd.getWarehouseService().addS3Credentials(warehouse, s3Credentials).get();
        assertThat(created, notNullValue());
        assertThat(created.getRegion(), is(REGION));
        assertThat(created.getAccessKey(), is(ACCESS_KEY));
        assertThat(created.getSecretKey(), is(nullValue()));
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Unable to add S3 credentials in warehouse, uri: /gdc/datawarehouse/instances/instanceId/s3/region/accessKey$")
    public void shouldFailToAddS3Credentials_failOnPoll() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_LIST_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().addS3Credentials(warehouse, s3Credentials).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Unable to POST S3 credentials /gdc/datawarehouse/instances/instanceId/s3 with region: region, access key: accessKey")
    public void shouldFailToAddS3Credentials_failOnPost() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_LIST_URI)
                .respond()
                .withStatus(409);

        gd.getWarehouseService().addS3Credentials(warehouse, s3Credentials).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Attempt to add S3 credentials in warehouse failed, can't get the result, uri: /gdc/datawarehouse/instances/instanceId/s3/region/accessKey$")
    public void shouldFailToAddS3Credentials_failOnResult() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_LIST_URI)
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
                .havingPathEqualTo(finishedTask.getWarehouseS3CredentialsUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().addS3Credentials(warehouse, s3Credentials).get();
    }


    @Test
    public void shouldUpdateS3Credentials() {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
                .havingBody(jsonEquals(readStringFromResource(S3_CREDENTIALS_UPDATE)))
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
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseS3CredentialsUri())
                .respond()
                .withBody(readFromResource("/warehouse/s3Credentials-get.json"))
                .withStatus(200);

        final WarehouseS3Credentials credentials = gd.getWarehouseService()
                .getWarehouseS3Credentials(warehouse, s3Credentials.getRegion(), s3Credentials.getAccessKey());
        credentials.setSecretKey(NEW_SECRET_KEY);

        final WarehouseS3Credentials updated = gd.getWarehouseService()
                .updateS3Credentials(credentials).get();
        assertThat(updated, notNullValue());
        assertThat(updated.getRegion(), is(REGION));
        assertThat(updated.getAccessKey(), is(ACCESS_KEY));
        assertThat(updated.getSecretKey(), is(nullValue()));
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Unable to update S3 credentials in warehouse, uri: /gdc/datawarehouse/instances/instanceId/s3/region/accessKey$")
    public void shouldFailToUpdateS3Credentials_failOnPoll() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().updateS3Credentials(s3CredentialsWithLinks).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Unable to PUT S3 credentials /gdc/datawarehouse/instances/instanceId/s3/region/accessKey with region: region, access key: accessKey")
    public void shouldFailToUpdateS3Credentials_failOnPut() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
                .respond()
                .withStatus(409);

        gd.getWarehouseService().updateS3Credentials(s3CredentialsWithLinks).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Attempt to update S3 credentials in warehouse failed, can't get the result, uri: /gdc/datawarehouse/instances/instanceId/s3/region/accessKey$")
    public void shouldFailToUpdateS3Credentials_failOnResult() {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
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
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseS3CredentialsUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().updateS3Credentials(s3CredentialsWithLinks).get();
    }

    @Test
    public void shouldRemoveS3Credentials() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
                .havingBodyEqualTo("")
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
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseS3CredentialsUri())
                .respond()
                .withBody(readFromResource("/warehouse/s3CredentialsList.json"))
                .withStatus(200);

        gd.getWarehouseService().removeS3Credentials(s3CredentialsWithLinks).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Unable to delete S3 credentials in warehouse, uri: /gdc/datawarehouse/instances/instanceId/s3/region/accessKey$")
    public void shouldFailToRemoveS3Credentials_failOnPoll() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
                .respond()
                .withBody(readFromResource(TASK_POLL))
                .withStatus(202);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(pollingTask.getPollUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().removeS3Credentials(s3CredentialsWithLinks).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Unable to DELETE S3 credentials /gdc/datawarehouse/instances/instanceId/s3/region/accessKey with region: region, access key: accessKey")
    public void shouldFailToRemoveS3Credentials_failOnDelete() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
                .respond()
                .withStatus(409);

        gd.getWarehouseService().removeS3Credentials(s3CredentialsWithLinks).get();
    }

    @Test(expectedExceptions = WarehouseS3CredentialsException.class,
            expectedExceptionsMessageRegExp = ".*Attempt to delete S3 credentials in warehouse failed, can't get the result, uri: /gdc/datawarehouse/instances/instanceId/s3/region/accessKey$")
    public void shouldFailToRemoveS3Credentials_failOnResult() {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(WAREHOUSE_S3_CREDENTIALS_URI)
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
                .withStatus(200);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(finishedTask.getWarehouseS3CredentialsUri())
                .respond()
                .withStatus(409);

        gd.getWarehouseService().removeS3Credentials(s3CredentialsWithLinks).get();
    }
}
