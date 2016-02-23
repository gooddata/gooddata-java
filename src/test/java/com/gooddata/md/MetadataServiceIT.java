/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.FutureResult;
import com.gooddata.JsonMatchers;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.maintenance.ExportImportException;
import com.gooddata.md.maintenance.PartialImportToken;
import com.gooddata.md.maintenance.PartialMdExport;
import com.gooddata.md.maintenance.PartialMdImport;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.util.Arrays.asList;
import static net.jadler.Jadler.onRequest;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;

public class MetadataServiceIT extends AbstractGoodDataIT {

    private static final String USEDBY_URI = "/gdc/md/PROJECT_ID/usedby2";
    private static final String IDENTIFIERS_URI = "/gdc/md/PROJECT_ID/identifiers";
    private static final String OBJ_URI = "/gdc/md/PROJECT_ID/obj";
    private static final String OBJ_URI2 = "/gdc/md/PROJECT_ID/obj2";
    private static final String SPECIFIC_OBJ_URI = "/gdc/md/PROJECT_ID/obj/ID";
    private static final String ID = "ID";
    private static final String TITLE = "TITLE";
    private static final String TITLE2 = "TITLE2";

    private Project project;
    private Metric metricInput;
    private ScheduledMail scheduledMailInput;

    @BeforeClass
    public void setUp() throws Exception {
        project = MAPPER.readValue(readFromResource("/project/project.json"), Project.class);
        metricInput = MAPPER.readValue(readFromResource("/md/metric-input.json"), Metric.class);
        scheduledMailInput = MAPPER.readValue(readFromResource("/md/scheduledMail.json"), ScheduledMail.class);
    }

    @Test
    public void testUsedBy() throws Exception {
        final List<UseManyEntries> entryList = new ArrayList<>();
        final Entry entry1 = new Entry(null, TITLE, null, null, null, null, false, null, null, null, null, false, false);
        entryList.add(new UseManyEntries(OBJ_URI, asList(entry1)));
        final UseMany useMany = new UseMany(entryList);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(USEDBY_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(useMany));

        final Collection<Entry> result = gd.getMetadataService().usedBy(project, OBJ_URI, false, ReportDefinition.class);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getTitle(), is(TITLE));
    }

    @Test
    public void testUsedByBatch() throws Exception {
        final List<UseManyEntries> entryList = new ArrayList<>();
        final Entry entry1 = new Entry(null, TITLE, null, null, null, null, false, null, null, null, null, false, false);
        final Entry entry2 = new Entry(null, TITLE2, null, null, null, null, false, null, null, null, null, false, false);
        entryList.add(new UseManyEntries(OBJ_URI, asList(entry1)));
        entryList.add(new UseManyEntries(OBJ_URI2, asList(entry1, entry2)));
        final UseMany useMany = new UseMany(entryList);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(USEDBY_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(useMany));

        final Collection<Usage> result = gd.getMetadataService().usedBy(project, new HashSet<>(asList(OBJ_URI, OBJ_URI2)), false, ReportDefinition.class);

        assertThat(result, hasSize(2));
        final Iterator<Usage> usages = result.iterator();

        final Usage usage1 = usages.next();
        assertThat(usage1.getUri(), is(OBJ_URI));
        assertThat(usage1.getUsedBy().iterator().next().getTitle(), is(TITLE));

        final Usage usage2 = usages.next();
        assertThat(usage2.getUri(), is(OBJ_URI2));
        final Iterator<Entry> obj2Entries = usage2.getUsedBy().iterator();
        assertThat(obj2Entries.next().getTitle(), is(TITLE));
        assertThat(obj2Entries.next().getTitle(), is(TITLE2));
    }

    @Test
    public void testFindIdentifierUris() throws IOException {
        final IdentifierAndUri identifierAndUri = new IdentifierAndUri(ID, OBJ_URI);

        final List<IdentifierAndUri> identifiersAndUris = new ArrayList<>();
        identifiersAndUris.add(identifierAndUri);
        IdentifiersAndUris response = new IdentifiersAndUris(identifiersAndUris);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(IDENTIFIERS_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(response));

        final Collection<String> uris = gd.getMetadataService().findUris(project, Restriction.identifier(ID));
        assertThat(uris, hasSize(1));
        assertThat(uris.iterator().next(), is(OBJ_URI));
    }

    @Test
    public void testIdentifiersToUris() throws IOException {
        final IdentifierAndUri identifierAndUri = new IdentifierAndUri(ID, OBJ_URI);

        final List<IdentifierAndUri> identifiersAndUris = new ArrayList<>();
        identifiersAndUris.add(identifierAndUri);
        IdentifiersAndUris response = new IdentifiersAndUris(identifiersAndUris);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(IDENTIFIERS_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(response));

        final Map<String, String> uris = gd.getMetadataService().identifiersToUris(project, asList(ID));
        assertThat(uris.keySet(), hasSize(1));
        assertThat(uris.get(ID), is(OBJ_URI));
    }

    @Test
    public void shouldCreateObj() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(OBJ_URI)
            .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new UriResponse(SPECIFIC_OBJ_URI)));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/metric.json"));

        final Obj result = gd.getMetadataService().createObj(project, metricInput);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(Metric.class)));
        assertThat(((Metric) result).getTitle(), is("Person Name"));
        assertThat(((Metric) result).getFormat(), is("FORMAT"));
    }

    @Test
    public void shouldUpdateObj() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingBodyEqualTo(MAPPER.writeValueAsString(metricInput))
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
            .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new UriResponse(SPECIFIC_OBJ_URI)));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/metric.json"));

        final Metric result = gd.getMetadataService().updateObj(metricInput);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(Metric.class)));
        assertThat(result.getTitle(), is("Person Name"));
        assertThat(result.getFormat(), is("FORMAT"));

    }

    @Test
    public void shouldRemoveObjByUri() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
                .respond()
                .withStatus(204);

        gd.getMetadataService().removeObjByUri(SPECIFIC_OBJ_URI);
    }

    @Test
    public void shouldRemoveObj() throws Exception {
        onRequest()
                .havingMethodEqualTo("DELETE")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
                .respond()
                .withStatus(204);

        gd.getMetadataService().removeObj(metricInput);
    }

    @Test
    public void shouldCreateMailScheduleObj() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(OBJ_URI)
                .respond()
                .withStatus(200)
                .withBody(MAPPER.writeValueAsString(new UriResponse(SPECIFIC_OBJ_URI)));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/scheduledMail.json"));

        final Obj result = gd.getMetadataService().createObj(project, scheduledMailInput);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(ScheduledMail.class)));
        assertThat(((ScheduledMail) result).getTitle(), is("Scheduled report example"));
        assertThat(((ScheduledMail) result).getToAddresses(), contains("email@example.com"));
    }

    @Test
    public void shouldGetObjByUri() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/metric.json"));

        final Obj result = gd.getMetadataService().getObjByUri(SPECIFIC_OBJ_URI, Metric.class);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(Metric.class)));
        assertThat(((Metric) result).getTitle(), is("Person Name"));
        assertThat(((Metric) result).getFormat(), is("FORMAT"));
    }

    @Test
    public void shouldGetObjById() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/metric.json"));

        final Obj result = gd.getMetadataService().getObjById(project, ID, Metric.class);

        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(Metric.class)));
        assertThat(result.getUri(), is("/gdc/md/PROJECT_ID/obj/DF_ID"));
        assertThat(((Metric) result).getTitle(), is("Person Name"));
        assertThat(((Metric) result).getFormat(), is("FORMAT"));
    }

    @Test
    public void shouldGetObjUriByRestrictions() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/query/attributes")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/query.json"));

        final String result = gd.getMetadataService().getObjUri(project, Attribute.class, Restriction.title("Resource"));

        assertThat(result, is(notNullValue()));
        assertThat(result, is("/gdc/md/PROJ_ID/obj/127"));
    }

    @Test
    public void shouldGetObjByRestrictions() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/query/attributes")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/query.json"));
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJ_ID/obj/118")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/attribute.json"));

        final Obj result = gd.getMetadataService().getObj(project, Attribute.class, Restriction.title("Name"));

        assertThat(result, is(notNullValue()));
        assertThat(result, is(instanceOf(Attribute.class)));
        assertThat(result.getUri(), is("/gdc/md/PROJECT_ID/obj/28"));
        assertThat(((Attribute) result).getTitle(), is("Person ID"));
    }

    @Test
    public void shouldFindByRestrictions() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/query/attributes")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/query.json"));

        final Collection<Entry> result = gd.getMetadataService()
                .find(project, Attribute.class, Restriction.summary(""));

        assertThat(result, is(notNullValue()));
        assertThat(result, hasSize(2));
    }

    @Test
    public void shouldFindUrisByRestrictions() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/query/attributes")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/query.json"));

        final Collection<String> result = gd.getMetadataService()
                .findUris(project, Attribute.class, Restriction.summary(""));

        assertThat(result, is(notNullValue()));
        assertThat(result, hasSize(2));
        assertThat(result, contains("/gdc/md/PROJ_ID/obj/127", "/gdc/md/PROJ_ID/obj/118"));
    }

    @Test
    public void shouldGetAttributeElements() throws Exception {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/obj/DF_ID/elements")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/attributeElements.json"));

        final List<AttributeElement> attributeElements =
                gd.getMetadataService().getAttributeElements(
                        readObjectFromResource("/md/attribute.json", Attribute.class));

        assertThat(attributeElements, is(Matchers.notNullValue()));
        assertThat(attributeElements, hasSize(3));
        assertThat(attributeElements.get(0).getTitle(), is("1167"));
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

        final FutureResult<PartialImportToken> partialExport = gd.getMetadataService()
                .partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234"));

        assertThat(partialExport.get().getToken(), is("TOKEN123"));
    }

    @Test(expectedExceptions = ExportImportException.class,
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

        gd.getMetadataService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
    }

    @Test(expectedExceptions = ExportImportException.class)
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

        gd.getMetadataService().partialExport(project, new PartialMdExport(false, false, "/gdc/md/projectId/obj/123", "/gdc/md/projectId/obj/234")).get();
    }

    @Test
    public void shouldImportPartialMetadata() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdImport.TEMPLATE.expand(project.getId()).toString())
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

        gd.getMetadataService().partialImport(project, new PartialMdImport("TOKEN123", true, true, true)).get();
    }

    @Test(expectedExceptions = ExportImportException.class,
            expectedExceptionsMessageRegExp = ".*The token \\(TOKEN123\\) is not valid.*")
    public void shouldPartialImportFailWhenErrorResult() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdImport.TEMPLATE.expand(project.getId()).toString())
        .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
        .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/maintenance/partial-import-task-status-fail.json"));

        gd.getMetadataService().partialImport(project, new PartialMdImport("TOKEN123", false, false, false)).get();
    }

    @Test(expectedExceptions = ExportImportException.class)
    public void shouldPartialImportFailWhenPollingError() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(PartialMdImport.TEMPLATE.expand(project.getId()).toString())
        .respond()
                .withStatus(200)
                .withBody("{ \"uri\": \"/gdc/md/projectId/tasks/taskId/status\" }");

        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/projectId/tasks/taskId/status")
        .respond()
                .withStatus(404);

        gd.getMetadataService().partialImport(project, new PartialMdImport("TOKEN123", false, false, false)).get();
    }
}
