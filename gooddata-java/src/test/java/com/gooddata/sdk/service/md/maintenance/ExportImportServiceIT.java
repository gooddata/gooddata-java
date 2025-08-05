/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md.maintenance;

import com.gooddata.sdk.model.md.maintenance.ExportProject;
import com.gooddata.sdk.model.md.maintenance.ExportProjectToken;
import com.gooddata.sdk.model.md.maintenance.PartialMdExport;
import com.gooddata.sdk.model.md.maintenance.PartialMdExportToken;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.springframework.web.util.UriTemplate;
import org.junit.jupiter.api.BeforeEach;        
import org.junit.jupiter.api.Test;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static net.javacrumbs.jsonunit.core.util.ResourceUtils.resource;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExportImportServiceIT extends AbstractGoodDataIT {

    private Project project;

    private static final UriTemplate PARTIAL_EXPORT_TEMPLATE = new UriTemplate(PartialMdExport.URI);
    private static final UriTemplate PARTIAL_TOKEN_TEMPLATE = new UriTemplate(PartialMdExportToken.URI);
    private static final UriTemplate FULL_EXPORT_TEMPLATE = new UriTemplate(ExportProject.URI);
    private static final UriTemplate FULL_TOKEN_TEMPLATE = new UriTemplate(ExportProjectToken.URI);

    @BeforeEach
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
    }

    @Test
    public void shouldExportPartialMetadata() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PARTIAL_EXPORT_TEMPLATE.expand(project.getId()).toString())
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

    @Test
    public void shouldPartialExportFailWhenErrorResult() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PARTIAL_EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-export-task-status-fail.json"));

        ExportImportException exception = assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
        });
        assertThat(exception.getMessage(), org.hamcrest.Matchers.containsString("The object with uri (/gdc/md/PROJECT_ID/obj/123) doesn't exists"));
    }

    @Test
    public void shouldPartialExportFailWhenPollingError() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PARTIAL_EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partialMDArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);
        assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
        });
    }

    @Test
    public void shouldImportPartialMetadata() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PARTIAL_TOKEN_TEMPLATE.expand(project.getId()).toString())
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

    @Test
    public void shouldPartialImportFailWhenErrorResult() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PARTIAL_TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-import-task-status-fail.json"));

        ExportImportException exception = assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().partialImport(project, new PartialMdExportToken("TOKEN123")).get();
        });
        assertThat(exception.getMessage(), org.hamcrest.Matchers.containsString("The token (TOKEN123) is not valid"));
    }

    @Test
    public void shouldPartialImportFailWhenPollingError() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PARTIAL_TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);
        assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().partialImport(project, new PartialMdExportToken("TOKEN123")).get();
        });
    }

    @Test
    public void shouldExportProject() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_EXPORT_TEMPLATE.expand(project.getId()).toString())
                .havingBody(jsonEquals(resource("md/maintenance/exportProject-defaultVals.json")).when(IGNORING_ARRAY_ORDER))
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/exportProjectArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/taskStateOK.json"));

        final ExportProjectToken partialExport = gd.getExportImportService().exportProject(project, new ExportProject()).get();

        assertThat(partialExport, notNullValue());
        assertThat(partialExport.getToken(), is("TOKEN123"));
    }

    @Test
    public void shouldExportProjectFailWhenErrorResult() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/exportProjectArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/taskStateError.json"));

        ExportImportException exception = assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().exportProject(project, new ExportProject()).get();
        });
        assertThat(exception.getMessage(), org.hamcrest.Matchers.containsString("Error message"));
    }

    @Test
    public void shouldExportProjectFailOnValidationError() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/exportProjectArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(400)
                .withBody(readFromResource("/gdc/gdcError.json"));

        ExportImportException exception = assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().exportProject(project, new ExportProject()).get();
        });
        assertThat(exception.getMessage(), org.hamcrest.Matchers.containsString("MSG: PARAM1, PARAM2"));
    }

    @Test
    public void shouldExportProjectFailWhenPollingError() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_EXPORT_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/exportProjectArtifact.json"));

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
                .respond()
                .withStatus(404);

        assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().exportProject(project, new ExportProject()).get();
        });
    }

    @Test
    public void shouldImportProject() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_TOKEN_TEMPLATE.expand(project.getId()).toString())
                .havingBody(jsonEquals(resource("md/maintenance/importProject.json")))
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/etltask/taskId\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/etltask/taskId")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/taskStateOK.json"));

        gd.getExportImportService().importProject(project, new ExportProjectToken("TOKEN123")).get();
    }

    @Test
    public void shouldProjectImportFailWhenErrorResult() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/etltask/taskId\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/etltask/taskId")
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/dataset/taskStateError.json"));

        ExportImportException exception = assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().importProject(project, new ExportProjectToken("TOKEN123")).get();
        });
        assertThat(exception.getMessage(), org.hamcrest.Matchers.containsString("Error message"));
    }

    @Test
    public void shouldProjectImportFailOnValidationError() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/etltask/taskId\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/etltask/taskId")
                .respond()
                .withStatus(400)
                .withBody(readFromResource("/gdc/gdcError.json"));

        ExportImportException exception = assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().importProject(project, new ExportProjectToken("TOKEN123")).get();
        });
        assertThat(exception.getMessage(), org.hamcrest.Matchers.containsString("MSG: PARAM1, PARAM2"));
    }

    @Test
    public void shouldProjectImportFailWhenPollingError() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(FULL_TOKEN_TEMPLATE.expand(project.getId()).toString())
                .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/etltask/taskId\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/etltask/taskId")
                .respond()
                .withStatus(404);

        assertThrows(ExportImportException.class, () -> {
            gd.getExportImportService().importProject(project, new ExportProjectToken("TOKEN123")).get();
        });
    }
}