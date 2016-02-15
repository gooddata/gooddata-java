package com.gooddata.md;

import static com.gooddata.md.Restriction.identifier;
import static com.gooddata.report.ReportExportFormat.PDF;
import static com.gooddata.report.ReportExportFormat.XLS;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.AssertJUnit.assertTrue;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.md.report.AttributeInGrid;
import com.gooddata.md.report.Filter;
import com.gooddata.md.report.GridElement;
import com.gooddata.md.report.GridReportDefinitionContent;
import com.gooddata.md.report.Report;
import com.gooddata.model.ModelDiff;
import com.gooddata.model.ModelService;
import com.gooddata.project.Environment;
import com.gooddata.project.Project;
import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Metadata acceptance tests.
 */
public class MetadataServiceAT extends AbstractGoodDataAT {

    private String partialExportToken;

    @Test(groups = "md", dependsOnGroups = "model")
    public void getObjs() throws Exception {
        final MetadataService md = gd.getMetadataService();

        fact = md.getObjUri(project, Fact.class, identifier("fact.person.shoesize"));
        attr = md.getObj(project, Attribute.class, identifier("attr.person.department"));
    }

    @Test(groups = "md", dependsOnMethods = "getObjs")
    public void updateObj() throws Exception {
        final MetadataService md = gd.getMetadataService();

        attr.setSummary("Changed person department");
        attr = md.updateObj(attr);
        assertThat(attr, is(notNullValue()));
        assertThat(attr.getSummary(), is("Changed person department"));
    }

    @Test(groups = "md", dependsOnMethods = "getObjs")
    public void createMetric() throws Exception {
        final MetadataService md = gd.getMetadataService();
        metric = md.createObj(project, new Metric("Avg shoe size", "SELECT AVG([" + fact + "])", "#,##0"));
    }

    @Test(groups = "md", dependsOnMethods = "createMetric")
    public void createReport() throws Exception {
        final MetadataService md = gd.getMetadataService();

        reportDefinition = md.createObj(project, GridReportDefinitionContent.create(
                "Department avg shoe size",
                asList("metricGroup"),
                asList(new AttributeInGrid(attr.getDefaultDisplayForm().getUri())),
                asList(new GridElement(metric.getUri(), "Avg shoe size")),
                asList(new Filter("(SELECT [" + metric.getUri() + "]) >= 0"))
        ));
        report = md.createObj(project, new Report(reportDefinition.getTitle(), reportDefinition));
    }

    @Test(groups = "md", dependsOnMethods = "createReport")
    public void usedBy() {
        final MetadataService metadataService = gd.getMetadataService();
        final String uri = metadataService.findUris(project, identifier("attr.person.department")).iterator().next();

        final Collection<Entry> usage = metadataService.usedBy(project, uri, false);
        assertThat(usage, hasSize(5));
    }

    @Test(groups = "md", dependsOnMethods = "createReport")
    public void usedByNotExists() {
        final MetadataService metadataService = gd.getMetadataService();

        assertThat(metadataService.usedBy(project, "/gdc/md/" + project.getId() + "/obj/1234567", false), empty());
    }

    @Test(groups = "md", dependsOnMethods = "createReport")
    public void usedByBatch() {
        final MetadataService metadataService = gd.getMetadataService();
        final Map<String, String> uris =
                metadataService.identifiersToUris(project, asList("attr.person.department", "attr.person.role"));

        final Collection<Usage> usages = metadataService.usedBy(project, uris.values(), false);
        assertThat(usages, hasSize(2));

        final Iterator<Usage> usageIterator = usages.iterator();

        final Usage usage1 = usageIterator.next();
        assertTrue(uris.containsValue(usage1.getUri()));
        assertThat(usage1.getUsedBy(), hasSize(2));

        final Usage usage2 = usageIterator.next();
        assertTrue(uris.containsValue(usage2.getUri()));
        assertThat(usage2.getUsedBy(), hasSize(5));
    }

    @Test(groups = "md", dependsOnMethods = "createReport")
    public void createScheduledMail() throws Exception {
        final MetadataService md = gd.getMetadataService();

        scheduledMail = md.createObj(project,
                (new ScheduledMail("Scheduled Mail Title", "Scheduled Mail Summary"))
                        .setRecurrency("0:0:0:1*12:0:0")
                        .setStartDate(new LocalDate(2012, 6, 5))
                        .setTimeZone("America/Los_Angeles")
                        .addToAddress(getProperty("login"))
                        .addBccAddress(getProperty("login"))
                        .setSubject("Mail subject")
                        .setBody("Mail body")
                        .addReportAttachment(reportDefinition,
                                Collections.singletonMap("pageOrientation", "landscape"),
                                PDF, XLS));
    }

    @Test(groups = "md", dependsOnMethods = "createScheduledMail")
    public void retrieveScheduledMail() throws Exception {
        final MetadataService md = gd.getMetadataService();
        Collection<Entry> result = md.find(project, ScheduledMail.class);
        assertThat(result, hasSize(1));
        for (Entry e : result) {
            ScheduledMail schedule = md.getObjByUri(e.getLink(), ScheduledMail.class);
        }
    }

    @Test(groups = "md", dependsOnMethods = "retrieveScheduledMail")
    public void removeScheduledMail() throws Exception {
        final MetadataService metadataService = gd.getMetadataService();
        metadataService.removeObj(scheduledMail);
    }

    @Test(groups = "md", dependsOnGroups = "model")
    public void identifiersToUri() {
        final MetadataService metadataService = gd.getMetadataService();
        final Map<String, String> idsToUris =
                metadataService.identifiersToUris(project, asList("attr.person.department"));
        assertThat(idsToUris.entrySet(), hasSize(1));
        final Attribute attribute = metadataService.getObjByUri(idsToUris.get("attr.person.department"), Attribute.class);
        assertThat(attribute.getIdentifier(), is("attr.person.department"));
    }

    @Test(dependsOnGroups = "report", dependsOnMethods = {"usedBy", "usedByBatch"})
    public void removeReport() throws Exception {
        final MetadataService metadataService = gd.getMetadataService();
        metadataService.removeObj(report);
    }

    @Test(dependsOnMethods = {"removeReport", "removeScheduledMail"})
    public void removeDefinition() throws Exception {
        final MetadataService metadataService = gd.getMetadataService();
        metadataService.removeObj(reportDefinition);
    }

    @Test(groups = "mdAfterLoad", dependsOnGroups = {"model", "dataset"}, dependsOnMethods = "getObjs")
    public void getAttributeElements() throws Exception {
        final List<AttributeElement> elements = gd.getMetadataService().getAttributeElements(attr);
        assertThat("there should be 2 elements", elements, hasSize(2));
        final Set<String> titles = new HashSet<>(elements.size());
        for (AttributeElement element : elements) {
            titles.add(element.getTitle());
        }
        assertThat(titles, hasItems("DevOps", "HR"));
    }

    @Test(groups = "md", dependsOnMethods = "createMetric")
    public void partialExportMetric() throws Exception {
        final String token = gd.getMetadataService().partialExport(project, Collections.singletonList(metric.getUri()), false, true).get();
        assertThat(token, not(isEmptyOrNullString()));
        partialExportToken = token;
    }

    @Test(groups = "md", dependsOnMethods = "partialExportMetric")
    public void partialImportDataset() throws Exception {
        final Project newProject = new Project(title + " - metadata import", projectToken);
        newProject.setEnvironment(Environment.TESTING);

        Project importProject = null;
        try {
            importProject = gd.getProjectService().createProject(newProject).get();

            final ModelService modelService = gd.getModelService();
            final ModelDiff projectModelDiff = modelService.getProjectModelDiff(importProject,
                    new InputStreamReader(getClass().getResourceAsStream("/person.json"))).get();
            modelService.updateProjectModel(importProject, projectModelDiff).get();

            gd.getMetadataService().partialImport(importProject, partialExportToken, true, true, true).get();
            final Metric importedMetric = gd.getMetadataService().getObjById(importProject,
                    StringUtils.substringAfterLast(metric.getUri(), "/"), Metric.class);

            assertThat(importedMetric, is(notNullValue()));
        } finally {
            if (importProject != null) {
                gd.getProjectService().removeProject(importProject);
            }
        }
    }
}
