/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataload;


import com.gooddata.sdk.service.GoodDataSettings;
import org.junit.jupiter.api.BeforeEach;    
import org.junit.jupiter.api.Test;  
import org.springframework.web.reactive.function.client.WebClient;  

import static org.junit.jupiter.api.Assertions.assertThrows;    

public class OutputStageServiceTest {

    private OutputStageService outputStageService;

    @BeforeEach     
    public void setUp() throws Exception {
        outputStageService = new OutputStageService(WebClient.builder().build(), new GoodDataSettings());   
    }

    @Test
    public void testGetOutputStageByNullUri() {
        assertThrows(IllegalArgumentException.class, () -> outputStageService.getOutputStageByUri(null));
    }

    @Test
    public void testGetOutputStageByNullProject() {
        assertThrows(IllegalArgumentException.class, () -> outputStageService.getOutputStage(null));
    }

    @Test
    public void testUpdateOutputStageNullOutputStage() {
        assertThrows(IllegalArgumentException.class, () -> outputStageService.updateOutputStage(null));
    }
}
