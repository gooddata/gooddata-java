package com.gooddata;

import com.gooddata.warehouse.Warehouse;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class WarehouseIdMatcher extends TypeSafeMatcher<Warehouse> {

    private final Warehouse warehouse;

    public WarehouseIdMatcher(final Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Warehouse id " + warehouse.getId());
    }

    public static WarehouseIdMatcher hasSameIdAs(final Warehouse warehouse) {
        return new WarehouseIdMatcher(warehouse);
    }

    @Override
    protected boolean matchesSafely(Warehouse item) {
        return warehouse.getId().equals(item.getId());
    }
}
