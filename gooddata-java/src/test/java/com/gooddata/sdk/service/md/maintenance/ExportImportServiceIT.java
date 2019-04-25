/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;

import com.gooddata.sdk.model.md.maintenance.PartialMdExport;
import com.gooddata.sdk.model.md.maintenance.PartialMdExportToken;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.springframework.web.util.UriTemplate;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExportImportServiceIT extends AbstractGoodDataIT {

    private Project project;

    private static final UriTemplate EXPORT_TEMPLATE = new UriTemplate(PartialMdExport.URI);
    private static final UriTemplate TOKEN_TEMPLATE = new UriTemplate(PartialMdExportToken.URI);

    @BeforeClass
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
    }

    @Test
    public void shouldExportPartialMetadata() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(EXPORT_TEMPLATE.expand(project.getId()).toString())
                .havingBody(jsonEquals(resource("md/maintenance/partialMDExport-defaultVals.json")).when(IGNORING_ARRAY_ORDER))
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/gdc/task-status.json"));

        final PartialMdExportToken partialExport = gd.getExportImportService()
                .partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();

        assertThat(partialExport, notNullValue());
        assertThat(partialExport.getToken(), is("TOKEN123"));
        assertThat(partialExport.isImportAttributeProperties(), is(false));
    }

    @Test(expectedExceptions = ExportImportException.class,
            expectedExceptionsMessageRegExp = ".*The object with uri \\(/gdc/md/PROJECT_ID/obj/123\\) doesn't exists.*")
    public void shouldPartialExportFailWhenErrorResult() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-export-task-status-fail.json"));

        gd.getExportImportService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
    }

    @Test(expectedExceptions = ExportImportException.class)
    public void shouldPartialExportFailWhenPollingError() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);

        gd.getExportImportService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
    }

    @Test
    public void shouldImportPartialMetadata() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(TOKEN_TEMPLATE.expand(project.getId()).toString())
                .havingBody(jsonEquals(resource("md/maintenance/partialMDImport.json")).when(IGNORING_ARRAY_ORDER))
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/gdc/task-status.json"));

        final PartialMdExportToken exportToken = new PartialMdExportToken("TOKEN123");
        exportToken.setUpdateLDMObjects(true);
        exportToken.setImportAttributeProperties(true);
        gd.getExportImportService().partialImport(project, exportToken).get();
    }

    @Test(expectedExceptions = ExportImportException.class,
            expectedExceptionsMessageRegExp = ".*The token \\(TOKEN123\\) is not valid.*")
    public void shouldPartialImportFailWhenErrorResult() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-import-task-status-fail.json"));

        gd.getExportImportService().partialImport(project, new PartialMdExportToken("TOKEN123")).get();
    }

    @Test(expectedExceptions = ExportImportException.class)
    public void shouldPartialImportFailWhenPollingError() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);

        gd.getExportImportService().partialImport(project, new PartialMdExportToken("TOKEN123")).get();
    }

}