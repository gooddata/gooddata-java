/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.warehouse;

import com.gooddata.FutureResult;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageableList;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

public class WarehouseServiceTest {

    private static final String WAREHOUSES_URL = "/gdc/datawarehouse/instances";
    private static final String WAREHOUSE_URL = "/gdc/datawarehouse/instances/17";
    private static final String WAREHOUSE_ID = "17";
    private static final String SCHEMAS_URL = "/gdc/datawarehouse/instances/17/schemas";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private Warehouse warehouse;

    private WarehouseService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(warehouse.getUri()).thenReturn(WAREHOUSE_URL);
        when(warehouse.getId()).thenReturn(WAREHOUSE_ID);
        service = new WarehouseService(restTemplate);
    }

    @Test
    public void testCreateWarehouse() throws Exception {
        final WarehouseTask warehouseTask = mock(WarehouseTask.class);
        when(restTemplate.postForObject(WAREHOUSES_URL, warehouse, WarehouseTask.class))
                .thenReturn(warehouseTask);
        when(warehouseTask.getPollUri()).thenReturn("TEST_POLL_URI");

        final FutureResult<Warehouse> result = service.createWarehouse(warehouse);

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testRemoveWarehouse() throws Exception {
        service.removeWarehouse(warehouse);
        verify(restTemplate).delete(WAREHOUSE_URL);
        verify(warehouse).getUri();
    }

    @Test
    public void testGetWarehouseByUri() throws Exception {
        when(restTemplate.getForObject("test-uri", Warehouse.class)).thenReturn(warehouse);
        assertThat(service.getWarehouseByUri("test-uri"), is(warehouse));
    }

    @Test
    public void testGetWarehouseById() throws Exception {
        when(restTemplate.getForObject(WAREHOUSE_URL, Warehouse.class)).thenReturn(warehouse);
        assertThat(service.getWarehouseById("17"), is(warehouse));
    }

    @Test
    public void testListWarehouses() throws Exception {
        when(restTemplate.getForObject(WAREHOUSES_URL, Warehouses.class)).thenReturn(mock(Warehouses.class));

        final PageableList<Warehouse> result = service.listWarehouses();

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testListWarehousesWithPage() throws Exception {
        final Page page = mock(Page.class);
        when(restTemplate.getForObject(WAREHOUSES_URL, Warehouses.class)).thenReturn(mock(Warehouses.class));

        final PageableList<Warehouse> result = service.listWarehouses(page);

        assertThat(result, is(notNullValue()));
        verify(page).getPageUri(any());
    }

    @Test
    public void testListWarehouseUsers() throws Exception {
        final Page page = mock(Page.class);
        final PageableList<WarehouseUser> result = service.listWarehouseUsers(warehouse, page);

        assertThat(result, is(notNullValue()));
        verify(page).getPageUri(any());
    }

    @Test
    public void testAddUserToWarehouse() throws Exception {
        final WarehouseUser user = mock(WarehouseUser.class);
        final WarehouseTask warehouseTask = mock(WarehouseTask.class);
        when(restTemplate.postForObject(WAREHOUSES_URL + "/{id}/users", user, WarehouseTask.class, WAREHOUSE_ID))
                .thenReturn(warehouseTask);
        when(warehouseTask.getPollUri()).thenReturn("TEST_POLL_URI");

        final FutureResult<WarehouseUser> result = service.addUserToWarehouse(warehouse, user);

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testRemoveUserFromWarehouse() throws Exception {
        final WarehouseUser user = mock(WarehouseUser.class);
        when(user.getUri()).thenReturn("TEST_USER_URI");
        final WarehouseTask warehouseTask = mock(WarehouseTask.class);
        when(warehouseTask.getPollUri()).thenReturn("TEST_POLL_URI");
        final ResponseEntity entity = mock(ResponseEntity.class);
        when(entity.getBody()).thenReturn(warehouseTask);
        when(restTemplate.exchange("TEST_USER_URI", HttpMethod.DELETE, null, WarehouseTask.class)).thenReturn(entity);

        final FutureResult<Void> result = service.removeUserFromWarehouse(user);
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testUpdateWarehouse() throws Exception {
        final Warehouse updatedWarehouse = mock(Warehouse.class);
        when(restTemplate.getForObject(WAREHOUSE_URL, Warehouse.class)).thenReturn(updatedWarehouse);

        final Warehouse result = service.updateWarehouse(this.warehouse);

        verify(restTemplate).put(WAREHOUSE_URL, this.warehouse);
        assertThat(result, is(updatedWarehouse));
    }

    @Test
    public void testListWarehouseSchemas() throws Exception {
        when(restTemplate.getForObject(SCHEMAS_URL, WarehouseSchemas.class)).thenReturn(mock(WarehouseSchemas.class));

        final PageableList<WarehouseSchema> result = service.listWarehouseSchemas(warehouse);

        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testGetWarehouseSchemaByName() throws Exception {
        final WarehouseSchema schema = mock(WarehouseSchema.class);
        when(restTemplate.getForObject(SCHEMAS_URL + "/TEST_NAME", WarehouseSchema.class)).thenReturn(schema);
        final WarehouseSchema result = service.getWarehouseSchemaByName(warehouse, "TEST_NAME");

        assertThat(result, is(schema));
    }

    @Test
    public void testGetWarehouseSchemaByUri() throws Exception {
        final WarehouseSchema schema = mock(WarehouseSchema.class);
        when(restTemplate.getForObject(SCHEMAS_URL + "/TEST_NAME", WarehouseSchema.class)).thenReturn(schema);
        final WarehouseSchema result = service.getWarehouseSchemaByUri(SCHEMAS_URL + "/TEST_NAME");

        assertThat(result, is(schema));
    }

    @Test
    public void testGetDefaultWarehouseSchema() throws Exception {
        final WarehouseSchema schema = mock(WarehouseSchema.class);
        when(restTemplate.getForObject(SCHEMAS_URL + "/default", WarehouseSchema.class)).thenReturn(schema);
        final WarehouseSchema result = service.getDefaultWarehouseSchema(warehouse);
        assertThat(result, is(schema));
    }

}