/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import org.springframework.web.client.RestTemplate;
import org.testng.annotations.Test;

public class ExportServiceTest {

    private final ExportService service = new ExportService(new RestTemplate());

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnNullArgument() throws Exception {
        new ExportService(null);
    }
}