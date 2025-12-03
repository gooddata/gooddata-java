/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.warehouse;

import com.gooddata.sdk.model.warehouse.Warehouse;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

class WarehouseIdMatcher extends TypeSafeMatcher<Warehouse> {

    private final Warehouse warehouse;

    public WarehouseIdMatcher(final Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public static WarehouseIdMatcher hasSameIdAs(final Warehouse warehouse) {
        return new WarehouseIdMatcher(warehouse);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("Warehouse id " + warehouse.getId());
    }

    @Override
    protected boolean matchesSafely(Warehouse item) {
        return warehouse.getId().equals(item.getId());
    }
}
