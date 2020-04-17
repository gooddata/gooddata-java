/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload;

import com.gooddata.sdk.service.GoodDataSettings;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OutputStageServiceTest {

    private OutputStageService outputStageService;

    @BeforeMethod
    public void setUp() throws Exception {
        outputStageService = new OutputStageService(new RestTemplate(), new GoodDataSettings());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetOutputStageByNullUri() {
        outputStageService.getOutputStageByUri(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetOutputStageByNullProject() {
        outputStageService.getOutputStage(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUpdateOutputStageNullOutputStage() {
        outputStageService.updateOutputStage(null);
    }
}