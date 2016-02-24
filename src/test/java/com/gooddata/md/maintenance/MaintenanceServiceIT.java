/*
 * Copyright (C) 2007-2016, GoodData(R) Corporation. All rights reserved.
 */

package com.gooddata.md.maintenance;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.JsonMatchers;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class MaintenanceServiceIT  extends AbstractGoodDataIT {

    private Project project;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
    }

    @Test
    public void shouldExportPartialMetadata() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdExport.TEMPLATE.expand(project.getId()).toString())
                .havingBody(JsonMatchers.isJsonString("/md/maintenance/partialMDExport-defaultVals.json"))
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/gdc/task-status.json"));

        final PartialMdExportToken partialExport = gd.getMaintenanceService()
                .partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();

        assertThat(partialExport, notNullValue());
        assertThat(partialExport.getToken(), is("TOKEN123"));
        assertThat(partialExport.isImportAttributeProperties(), is(false));
    }

    @Test(expectedExceptions = MaintenanceException.class,
            expectedExceptionsMessageRegExp = ".*The object with uri \\(/gdc/md/PROJECT_ID/obj/123\\) doesn't exists.*")
    public void shouldPartialExportFailWhenErrorResult() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdExport.TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-export-task-status-fail.json"));

        gd.getMaintenanceService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
    }

    @Test(expectedExceptions = MaintenanceException.class)
    public void shouldPartialExportFailWhenPollingError() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdExport.TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);

        gd.getMaintenanceService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
    }

    @Test
    public void shouldImportPartialMetadata() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdExportToken.TEMPLATE.expand(project.getId()).toString())
                .havingBody(JsonMatchers.isJsonString("/md/maintenance/partialMDImport.json"))
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/gdc/task-status.json"));

        gd.getMaintenanceService().partialImport(project, new PartialMdExportToken("TOKEN123", true, true, true)).get();
    }

    @Test(expectedExceptions = MaintenanceException.class,
            expectedExceptionsMessageRegExp = ".*The token \\(TOKEN123\\) is not valid.*")
    public void shouldPartialImportFailWhenErrorResult() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdExportToken.TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-import-task-status-fail.json"));

        gd.getMaintenanceService().partialImport(project, new PartialMdExportToken("TOKEN123", false, false, false)).get();
    }

    @Test(expectedExceptions = MaintenanceException.class)
    public void shouldPartialImportFailWhenPollingError() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdExportToken.TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);

        gd.getMaintenanceService().partialImport(project, new PartialMdExportToken("TOKEN123", false, false, false)).get();
    }

}