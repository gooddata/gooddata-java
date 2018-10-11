/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.account.Account;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import com.gooddata.project.Environment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.gooddata.warehouse.WarehouseIdMatcher.hasSameIdAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Warehouse acceptance tests
 */

public class WarehouseServiceAT extends AbstractGoodDataAT {

    private static final String LOGIN = "john.smith." + UUID.randomUUID() + "@gooddata.com";
    private static final String SCHEMA_NAME = "default";

    private final String warehouseToken;
    private final WarehouseService service;

    private Warehouse warehouse;
    private Warehouse warehouse2;

    private Account account;
    private WarehouseUser warehouseUser;

    public WarehouseServiceAT() {
        warehouseToken = getProperty("warehouseToken");
        service = gd.getWarehouseService();
    }

    @BeforeClass(groups = "isolated_domain")
    public void initIsolatedDomainGroup() {
        account = gd.getAccountService().createAccount(new Account(LOGIN, "nnPvcGXU7f", "FirstName", "LastName"), getProperty("domain"));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void getWarehouse() throws Exception {
        final Warehouse warehouse = service.getWarehouseById(this.warehouse.getId());
        assertThat(warehouse, is(hasSameIdAs(warehouse)));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void listWarehouses() throws Exception {
        final LinkedList<Warehouse> result = new LinkedList<>();
        Page page = new PageRequest(1000);
        PageableList<Warehouse> warehouses;

        do {
            warehouses = service.listWarehouses(page);
            result.addAll(warehouses);
            page = warehouses.getNextPage();
        } while (warehouses.hasNextPage());

        assertThat(result, hasItem(hasSameIdAs(warehouse)));
    }

    @Test(groups = "warehouse", dependsOnMethods = {"createWarehouse", "listWarehouses"})
    public void shouldPageList() throws Exception {
        final String title = this.title + " second";
        final Warehouse wh = new Warehouse(title, warehouseToken);
        wh.setEnvironment(Environment.TESTING);
        warehouse2 = service.createWarehouse(wh).get();

        final PageableList<Warehouse> firstPage = service.listWarehouses(new PageRequest(1));
        assertThat(firstPage, hasSize(1));

        PageableList<Warehouse> page = service.listWarehouses(firstPage.getNextPage());
        assertThat(page, hasSize(1));
    }

    @Test(groups = "warehouse", dependsOnMethods = "shouldPageList")
    public void shouldReturnNullOnEndOfPaging() throws Exception {
        PageableList<Warehouse> page = service.listWarehouses();
        Page nextPage;
        while ((nextPage = page.getNextPage()) != null) {
            page = service.listWarehouses(nextPage);
        }
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void shouldListUsers() throws Exception {
        final PageableList<WarehouseUser> users = service.listWarehouseUsers(warehouse, new PageRequest(1));
        assertThat(users, hasSize(1));
        assertThat(users.get(0), is(notNullValue()));
        assertThat(users.getNextPage(), is(nullValue()));
    }

    @Test(groups = { "warehouse", "isolated_domain" }, dependsOnMethods = "shouldListUsers")
    public void shouldAddUserToWarehouse() {
        warehouseUser = service.addUserToWarehouse(warehouse, WarehouseUser.createWithlogin(LOGIN, WarehouseUserRole.ADMIN)).get(60, TimeUnit.SECONDS);

        assertThat(warehouseUser, is(notNullValue()));
    }

    @Test(groups = { "warehouse", "isolated_domain" }, dependsOnMethods = "shouldAddUserToWarehouse")
    public void shouldRemoveUserFromWarehouse() {
        service.removeUserFromWarehouse(warehouseUser).get(60, TimeUnit.SECONDS);
        warehouseUser = null;
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void listWarehouseSchemas() {
        PageableList<WarehouseSchema> warehouseSchemas = service.listWarehouseSchemas(warehouse);
        assertThat(warehouseSchemas, contains(hasProperty("name", equalTo(SCHEMA_NAME))));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void getWarehouseSchema() {
        WarehouseSchema warehouseSchema = service.getWarehouseSchemaByName(warehouse, SCHEMA_NAME);
        assertThat(warehouseSchema, is(notNullValue()));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void getDefaultWarehouseSchema() {
        WarehouseSchema warehouseSchema = service.getDefaultWarehouseSchema(warehouse);
        assertThat(warehouseSchema, is(notNullValue()));
    }

    @Test(dependsOnGroups = "warehouse")
    public void removeWarehouse() throws Exception {
        service.removeWarehouse(warehouse);
        warehouse = null;
        service.removeWarehouse(warehouse2);
        warehouse2 = null;
    }

    @AfterClass
    public void tearDown() throws Exception {
        try {
            if (warehouse != null) {
                service.removeWarehouse(warehouse);
            }
        } catch (Exception ignored) {}
        try {
            if (warehouse2 != null) {
                service.removeWarehouse(warehouse2);
            }
        } catch (Exception ignored) {}
    }

    @AfterClass(groups = "isolated_domain")
    public void tearDownIsolatedDomainGroup() {
        try {
            if (warehouseUser != null) {
                service.removeUserFromWarehouse(warehouseUser);
            }
        }  catch (Exception ignored) {}
        try {
            if (account != null) {
                gd.getAccountService().removeAccount(account);
            }
        } catch (Exception ignored) {}
    }
}
