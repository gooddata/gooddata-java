/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.warehouse;

import org.testng.annotations.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class WarehouseS3CredentialsExceptionTest {

    public static final String URI = "/some/uri";

    @Test
    public void getUri() {
        assertThat(new WarehouseS3CredentialsException(URI, "message").getUri(), is(URI));
    }
}
