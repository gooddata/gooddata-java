/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.export;

import com.gooddata.GoodDataEndpoint;
import com.gooddata.md.ProjectDashboard;
import com.gooddata.md.ProjectDashboard.Tab;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;

import static com.gooddata.export.ExportService.extractProjectId;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExportServiceTest {

    private final ExportService service = new ExportService(new RestTemplate(), new GoodDataEndpoint());
    private ProjectDashboard dashboard;
    private Tab tab;

    @BeforeMethod
    public void setUp() throws Exception {
        dashboard = readObjectFromResource("/md/projectDashboard.json", ProjectDashboard.class);
        tab = dashboard.getTabs().iterator().next();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldFailOnNullArgument() throws Exception {
        new ExportService(null, new GoodDataEndpoint());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*endpoint.*")
    public void shouldFailOnNullEndpoint() throws Exception {
        new ExportService(new RestTemplate(), null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*dashboard.*")
    public void shouldFailExportPdfOnNullDashboard() throws Exception {
        service.exportPdf(null, tab, new ByteArrayOutputStream());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*tab.*")
    public void shouldFailExportPdfOnNullTab() throws Exception {
        service.exportPdf(dashboard, null, new ByteArrayOutputStream());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*output.*")
    public void shouldFailExportPdfOnNullStream() throws Exception {
        service.exportPdf(dashboard, tab, null);
    }

    @Test
    public void shouldExtractProjectId() throws Exception {
        assertThat(extractProjectId(dashboard), is("PROJECT_ID"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*dashboard.uri.*")
    public void shouldFailExtractProjectIdOnNullDashboardUri() throws Exception {
        final ProjectDashboard dashboard = mock(ProjectDashboard.class);
        extractProjectId(dashboard);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*dashboard uri - project id.*")
    public void shouldFailExtractProjectIdOnInvalidDashboardUri() throws Exception {
        final ProjectDashboard dashboard = mock(ProjectDashboard.class);
        when(dashboard.getUri()).thenReturn("/foo");
        extractProjectId(dashboard);
    }
}