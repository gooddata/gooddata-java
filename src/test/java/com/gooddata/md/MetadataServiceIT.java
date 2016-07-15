/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.AbstractGoodDataIT;
import com.gooddata.gdc.UriResponse;
import com.gooddata.md.report.ReportDefinition;
import com.gooddata.project.Project;
import com.gooddata.util.ResourceUtils;
import net.javacrumbs.jsonunit.JsonMatchers;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.util.ResourceUtils.readStringFromResource;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
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
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(IDENTIFIERS_URI)
                .havingBody(jsonEquals(readStringFromResource("/md/identifierToUri.json")))
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/identifiersAndUris.json"));

        final Collection<String> uris = gd.getMetadataService().findUris(project, Restriction.identifier(ID));
        assertThat(uris, hasSize(1));
        assertThat(uris.iterator().next(), is(OBJ_URI));
    }

    @Test
    public void testIdentifiersToUris() throws IOException {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(IDENTIFIERS_URI)
                .havingBody(jsonEquals(readStringFromResource("/md/identifierToUri.json")))
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/identifiersAndUris.json"));

        final Map<String, String> uris = gd.getMetadataService().identifiersToUris(project, singletonList(ID));
        assertThat(uris.keySet(), hasSize(1));
        assertThat(uris.get(ID), is(OBJ_URI));
    }

    @Test
    public void shouldCreateObj() throws Exception {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(OBJ_URI)
                .havingParameterEqualTo("createAndGet", "true")
            .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/metric-created.json"));

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
                .havingParameterEqualTo("createAndGet", "true")
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
}
