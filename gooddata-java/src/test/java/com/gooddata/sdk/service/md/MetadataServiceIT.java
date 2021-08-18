/*
 * Copyright (C) 2004-2021, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.service.AbstractGoodDataIT;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.md.*;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.project.Project;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.*;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static com.gooddata.sdk.common.util.ResourceUtils.readStringFromResource;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static net.jadler.Jadler.onRequest;
import static net.javacrumbs.jsonunit.JsonMatchers.jsonEquals;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;

public class MetadataServiceIT extends AbstractGoodDataIT {

    private static final String USEDBY_URI = "/gdc/md/PROJECT_ID/usedby2";
    private static final String IDENTIFIERS_URI = "/gdc/md/PROJECT_ID/identifiers";
    private static final String OBJ_URI = "/gdc/md/PROJECT_ID/obj";
    private static final String OBJ_URI2 = "/gdc/md/PROJECT_ID/obj2";
    private static final String SPECIFIC_OBJ_URI = "/gdc/md/PROJECT_ID/obj/ID";
    private static final String BULK_GET_URI = "/gdc/md/PROJECT_ID/objects/get";
    private static final String ID = "ID";
    private static final String TITLE = "TITLE";
    private static final String TITLE2 = "TITLE2";
    private static final String METRIC_URL = "/gdc/md/PROJECT_ID/obj/METRIC_ID";
    private static final String FACT_URL = "/gdc/md/PROJECT_ID/obj/FACT_ID";
    private static final String DATASET_URL = "/gdc/md/PROJECT_ID/obj/DATASET_ID";

    private Project project;
    private Metric metricInput;
    private ScheduledMail scheduledMailInput;

    @BeforeClass
    public void setUp() throws Exception {
        project = readObjectFromResource("/project/project.json", Project.class);
        metricInput = readObjectFromResource("/md/metric-input.json", Metric.class);
        scheduledMailInput = readObjectFromResource("/md/scheduledMail.json", ScheduledMail.class);
    }

    @Test
    public void testUsedBy() throws Exception {
        final UseMany useMany = OBJECT_MAPPER
                .readValue(format("{\"useMany\":[{\"uri\":\"%s\", \"entries\":[{\"title\":\"%s\"}]}]}", OBJ_URI, TITLE),
                        UseMany.class);

                onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(USEDBY_URI)
                .respond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(useMany));

        final Collection<Entry> result = gd.getMetadataService().usedBy(project, OBJ_URI, false, ReportDefinition.class);

        assertThat(result, hasSize(1));
        assertThat(result.iterator().next().getTitle(), is(TITLE));
    }

    @Test
    public void testGetObjsByUris() {
        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(BULK_GET_URI)
                .havingBody(allOf(containsString(DATASET_URL), containsString(FACT_URL), containsString(METRIC_URL)))
                .respond()
                .withStatus(200)
                .withBody(readFromResource("/md/bulk-get.json"));

        final Collection<Obj> result = gd.getMetadataService().getObjsByUris(project, Arrays.asList(
                DATASET_URL,
                FACT_URL,
                METRIC_URL
        ));

        Dataset dataset = null;
        Fact fact = null;
        Metric metric = null;

        for (Obj obj : result) {
            if (obj instanceof Dataset) {
                dataset = (Dataset) obj;
            } else if (obj instanceof Fact) {
                fact = (Fact) obj;
            } else if (obj instanceof Metric) {
                metric = (Metric) obj;
            }
        }

        assertThat(result, hasSize(3));
        assertThat(dataset, is(notNullValue()));
        //noinspection ConstantConditions
        assertThat(dataset.getTitle(), is("Date (Org minDate-first deal)"));
        assertThat(fact, is(notNullValue()));
        //noinspection ConstantConditions
        assertThat(fact.getTitle(), is("Person Shoe Size"));
        assertThat(metric, is(notNullValue()));
        //noinspection ConstantConditions
        assertThat(metric.getTitle(), is("Person Name"));
    }

    @Test
    public void testUsedByBatch() throws Exception {
        final UseMany useMany = OBJECT_MAPPER
                .readValue(
                        format("{\"useMany\":[{\"uri\":\"%s\", \"entries\":[{\"title\":\"%s\"}]}," +
                                        "{\"uri\":\"%s\", \"entries\":[{\"title\":\"%s\"}, {\"title\":\"%s\"}]}]}",
                                OBJ_URI, TITLE, OBJ_URI2, TITLE, TITLE2),
                        UseMany.class);

        onRequest()
                .havingMethodEqualTo("POST")
                .havingPathEqualTo(USEDBY_URI)
                .respond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(useMany));

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
                .havingBodyEqualTo(OBJECT_MAPPER.writeValueAsString(metricInput))
                .havingPathEqualTo(SPECIFIC_OBJ_URI)
            .respond()
                .withStatus(200)
                .withBody(OBJECT_MAPPER.writeValueAsString(new UriResponse(SPECIFIC_OBJ_URI)));
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

    @Test(expectedExceptions = GoodDataException.class, expectedExceptionsMessageRegExp = ".*request_id.*Unauthorized.*")
    public void getTimezoneShouldThrowGDEOnClientError() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/service/timezone")
            .respond()
                .withStatus(401);

        gd.getMetadataService().getTimezone(project);
    }

    @Test(expectedExceptions = GoodDataException.class, expectedExceptionsMessageRegExp = ".*request_id.*Server Error.*")
    public void getTimezoneShouldThrowGDEOnServerError() {
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc/md/PROJECT_ID/service/timezone")
            .respond()
                .withStatus(500);

        gd.getMetadataService().getTimezone(project);
    }
}
