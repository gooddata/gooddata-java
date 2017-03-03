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
import static org.testng.Assert.*;

public class WarehouseUserNotFoundExceptionTest {

    @Test
    public void testGetUserUri() throws Exception {
        final WarehouseUserNotFoundException exception = new WarehouseUserNotFoundException("TEST",
                mock(GoodDataRestException.class));
        assertThat(exception.getUserUri(), is("TEST"));
    }

}