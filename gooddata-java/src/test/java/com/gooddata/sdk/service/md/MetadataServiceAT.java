/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.model.md.Attribute;
import com.gooddata.sdk.model.md.AttributeElement;
import com.gooddata.sdk.model.md.Entry;
import com.gooddata.sdk.model.md.Fact;
import com.gooddata.sdk.model.md.Metric;
import com.gooddata.sdk.model.md.Obj;
import com.gooddata.sdk.model.md.ProjectDashboard;
import com.gooddata.sdk.model.md.ProjectDashboard.Tab;
import com.gooddata.sdk.model.md.ScheduledMail;
import com.gooddata.sdk.model.md.Usage;
import com.gooddata.sdk.model.md.report.AttributeInGrid;
import com.gooddata.sdk.model.md.report.Filter;
import com.gooddata.sdk.model.md.report.GridReportDefinitionContent;
import com.gooddata.sdk.model.md.report.MetricElement;
import com.gooddata.sdk.model.md.report.Report;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.gooddata.sdk.model.export.ExportFormat.PDF;
import static com.gooddata.sdk.model.export.ExportFormat.XLS;
import static com.gooddata.sdk.model.md.Restriction.identifier;
import static com.gooddata.sdk.model.md.report.MetricGroup.METRIC_GROUP;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Metadata acceptance tests.
 */
public class MetadataServiceAT extends AbstractGoodDataAT {

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
        final Metric create = new Metric("Avg shoe size", "SELECT AVG([" + fact + "])", "#,##0");
        create.setIdentifier("metric.avgshoesize");
        metric = md.createObj(project, create);
        assertThat(metric.getIdentifier(), is("metric.avgshoesize"));
    }

    @Test(groups = "md", dependsOnMethods = "createMetric")
    public void createReport() throws Exception {
        final MetadataService md = gd.getMetadataService();

        reportDefinition = md.createObj(project, GridReportDefinitionContent.create(
                "Department avg shoe size",
                asList(METRIC_GROUP),
                asList(new AttributeInGrid(attr.getDefaultDisplayForm())),
                asList(new MetricElement(metric, "Avg shoe size")),
                asList(new Filter("(SELECT [" + metric.getUri() + "]) >= 0"))
        ));
        report = md.createObj(project, new Report(reportDefinition.getTitle(), reportDefinition));
    }

    @Test(groups = "md", dependsOnGroups = "model")
    public void createDashboardEmpty() throws Exception {
        dashboard = gd.getMetadataService().createObj(project, new ProjectDashboard("My Dashboard", new Tab("My Tab")));

        assertThat(dashboard.getTitle(), is("My Dashboard"));
        assertThat(dashboard.getTabs(), hasSize(1));
        final Tab tab = dashboard.getTabs().iterator().next();
        assertThat(tab.getTitle(), is("My Tab"));
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

    @Test(groups = "md", dependsOnGroups = "model")
    public void getObjsByUris() throws Exception {
        final MetadataService md = gd.getMetadataService();

        final Map<String, String> uris =
                md.identifiersToUris(project, asList(
                        "attr.person.department",
                        "attr.person.role",
                        "fact.person.shoesize"
                ));

        final Collection<Obj> objects = md.getObjsByUris(project, uris.values());
        assertThat(objects.stream().map(Obj::getUri).collect(Collectors.toSet()), is(new HashSet<>(uris.values())));
    }

    @Test(groups = "md", dependsOnMethods = "createReport")
    public void createScheduledMail() throws Exception {
        final MetadataService md = gd.getMetadataService();

        scheduledMail = md.createObj(project,
                (new ScheduledMail("Scheduled Mail Title", "Scheduled Mail Summary"))
                        .setRecurrency("0:0:0:1*12:0:0")
                        .setStartDate(LocalDate.of(2012, 6, 5))
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
            ScheduledMail schedule = md.getObjByUri(e.getUri(), ScheduledMail.class);
            assertThat(schedule.getTitle(), is("Scheduled Mail Title"));
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

    @Test(dependsOnGroups = "export", dependsOnMethods = {"usedBy", "usedByBatch"})
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
        titles.addAll(elements.stream().map(AttributeElement::getTitle).collect(Collectors.toList()));
        assertThat(titles, hasItems("DevOps", "HR"));
    }

    @Test(groups = "md", dependsOnGroups = "project")
    public void getTimezone() {
        final MetadataService md = gd.getMetadataService();

        final String tz = md.getTimezone(project);
        assertThat(tz, is("America/Los_Angeles"));
    }

}
