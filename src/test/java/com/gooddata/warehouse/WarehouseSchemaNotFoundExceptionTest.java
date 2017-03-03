/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */

package com.gooddata.warehouse;

import com.gooddata.GoodDataRestException;
import org.testng.annotations.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;

public class WarehouseSchemaNotFoundExceptionTest {

    @Test
    public void testGetWarehouseSchemaUri() throws Exception {
        final WarehouseSchemaNotFoundException exception = new WarehouseSchemaNotFoundException("TEST",
                mock(GoodDataRestException.class));
        assertThat(exception.getWarehouseSchemaUri(), is("TEST"));
    }

}