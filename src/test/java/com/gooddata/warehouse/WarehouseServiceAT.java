package com.gooddata.warehouse;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.project.Environment;
import org.testng.annotations.Test;

import java.util.Collection;

import static com.gooddata.warehouse.WarehouseIdMatcher.hasSameIdAs;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

/**
 * Warehouse acceptance tests
 */
public class WarehouseServiceAT extends AbstractGoodDataAT {

    private final String warehouseToken;
    private Warehouse warehouse;

    public WarehouseServiceAT() {
        warehouseToken = getProperty("warehouseToken");
    }

    @Test(groups = "warehouse", dependsOnGroups = "account")
    public void createWarehouse() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        final Warehouse wh = new Warehouse(title, warehouseToken);
        wh.setEnvironment(Environment.TESTING);
        warehouse = warehouseService.createWarehouse(wh).get();
        String jdbc = warehouse.getJdbcConnectionString();
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void getWarehouse() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        final Warehouse warehouse = warehouseService.getWarehouseById(this.warehouse.getId());
        assertThat(warehouse, is(hasSameIdAs(warehouse)));
    }

    @Test(groups = "warehouse", dependsOnMethods = "createWarehouse")
    public void listWarehouses() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        final Collection<Warehouse> warehouses = warehouseService.listWarehouses();
        assertThat(warehouses, hasItem(hasSameIdAs(warehouse)));
    }

    @Test(dependsOnGroups = "warehouse")
    public void removeWarehouse() throws Exception {
        final WarehouseService warehouseService = gd.getWarehouseService();
        warehouseService.removeWarehouse(warehouse);
    }
}
