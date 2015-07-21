package com.gooddata.warehouse;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.collections.Page;
import com.gooddata.collections.PageRequest;
import com.gooddata.collections.PageableList;
import com.gooddata.project.Environment;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.warehouse.WarehouseIdMatcher.hasSameIdAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Warehouse acceptance tests
 */
public class WarehouseServiceAT extends AbstractGoodDataAT {

    private final String warehouseToken;
    private final WarehouseService service;

    private Warehouse warehouse;
    private Warehouse warehouse2;

    public WarehouseServiceAT() {
        warehouseToken = getProperty("warehouseToken");
        service = gd.getWarehouseService();
    }

    @Test(groups = "warehouse", dependsOnGroups = "account")
    public void createWarehouse() throws Exception {
        final Warehouse wh = new Warehouse(title, warehouseToken);
        wh.setEnvironment(Environment.TESTING);
        warehouse = service.createWarehouse(wh).get();
        String jdbc = warehouse.getJdbcConnectionString();
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void getWarehouse() throws Exception {
        final Warehouse warehouse = service.getWarehouseById(this.warehouse.getId());
        assertThat(warehouse, is(hasSameIdAs(warehouse)));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void listWarehouses() throws Exception {
        final Collection<Warehouse> warehouses = service.listWarehouses();
        assertThat(warehouses, hasItem(hasSameIdAs(warehouse)));
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
}
