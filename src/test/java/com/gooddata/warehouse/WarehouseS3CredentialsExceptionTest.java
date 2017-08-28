/*
 * Copyright (C) 2007-2017, GoodData(R) Corporation. All rights reserved.
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
