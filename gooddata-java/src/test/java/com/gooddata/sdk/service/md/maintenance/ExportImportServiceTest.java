/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.md.maintenance.PartialMdArtifact;
import com.gooddata.sdk.model.md.maintenance.PartialMdExport;
import com.gooddata.sdk.model.md.maintenance.PartialMdExportToken;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class ExportImportServiceTest {

    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;
    @Mock
    private RestTemplate restTemplate;

    private ExportImportService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new ExportImportService(restTemplate, new GoodDataSettings());
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test(expectedExceptions = ExportImportException.class)
    public void testCreatePartialExportError() throws Exception {
        when(restTemplate.postForObject(eq(PartialMdExport.URI), any(PartialMdExport.class), eq(PartialMdArtifact.class), eq(PROJECT_ID)))
                .thenThrow(new GoodDataRestException(400, "request", "Failed", "export", "error"));

        service.partialExport(project, new PartialMdExport(false, false, "uri123"));
    }

    @Test(expectedExceptions = ExportImportException.class)
    public void testCreatePartialImportError() throws Exception {
        when(restTemplate.postForObject(eq(PartialMdExportToken.URI), any(PartialMdExportToken.class), eq(UriResponse.class), eq(PROJECT_ID)))
                .thenThrow(new GoodDataRestException(400, "request", "Failed", "import", "error"));

        service.partialImport(project, new PartialMdExportToken("TOKEN123"));
    }
}