/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.md.*;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.md.visualization.VisualizationClass;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.gooddata.util.ResourceUtils.readObjectFromResource;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

public class MetadataServiceTest {

    private static final String URI = "TEST_URI";
    private static final String ID = "TEST_ID";
    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;
    @Mock
    private RestTemplate restTemplate;

    private MetadataService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new MetadataService(restTemplate, new GoodDataSettings());
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test(expectedExceptions = ObjCreateException.class)
    public void testCreateObjNullResponse() {
        final Obj obj = mock(Obj.class);
        service.createObj(project, obj);
    }

    @Test(expectedExceptions = ObjCreateException.class)
    @SuppressWarnings("unchecked")
    public void testCreateObjGDRestException() {
        final Obj obj = mock(Obj.class);
        when(restTemplate.postForObject(Obj.URI, obj, UriResponse.class, PROJECT_ID))
                .thenThrow(GoodDataRestException.class);
        service.createObj(project, obj);
    }

    @Test(expectedExceptions = ObjCreateException.class)
    public void testCreateObjRestClientException() {
        final Obj obj = mock(Obj.class);
        when(restTemplate.postForObject(Obj.URI, obj, UriResponse.class, PROJECT_ID))
                .thenThrow(new RestClientException(""));
        service.createObj(project, obj);
    }

    @Test
    public void testCreateObj() {
        final Obj obj = mock(Obj.class);
        final Obj resultObj = mock(Obj.class);

        when(restTemplate.postForObject(eq(Obj.CREATE_WITH_ID_URI), eq(obj), Matchers.<Class<Obj>>any(), eq(PROJECT_ID)))
                .thenReturn(resultObj);

        final Obj result = service.createObj(project, obj);
        assertThat(result, is(notNullValue()));
    }

    @Test
    public void testUpdateObj() {
        final Updatable obj = mock(Updatable.class);
        final Updatable resultObj = mock(Updatable.class);

        when(obj.getUri()).thenReturn(URI);
        final Updatable forObject = restTemplate.getForObject(URI, obj.getClass());
        when(forObject).thenReturn(resultObj);

        final Obj result = service.updateObj(obj);

        verify(restTemplate).put(obj.getUri(), obj);
        assertThat(result, is(notNullValue()));
    }

    @Test(expectedExceptions = ObjUpdateException.class)
    public void testUpdateObjNotFound() {
        final Updatable obj = mock(Updatable.class);

        final GoodDataRestException restException = mock(GoodDataRestException.class);
        when(restException.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());

        when(obj.getUri()).thenReturn(URI);
        when(restTemplate.getForObject(URI, obj.getClass())).thenThrow(restException);

        service.updateObj(obj);
    }

    @Test(expectedExceptions = ObjUpdateException.class)
    public void testUpdateObjRestException() {
        final Updatable obj = mock(Updatable.class);

        when(obj.getUri()).thenReturn(URI);
        doThrow(RestClientException.class).when(restTemplate).put(URI, obj);

        service.updateObj(obj);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjsByUrisNullProject() {
        service.getObjsByUris(null, Collections.emptyList());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjsByUrisNullUris() {
        service.getObjsByUris(project, null);
    }

    @Test
    public void testGetObjsByUris() {
        final BulkGetUris request = new BulkGetUris(Collections.singletonList(URI));
        final BulkGet response = readObjectFromResource("/md/bulk-get.json", BulkGet.class);
        when(restTemplate.postForObject(BulkGet.URI, request, BulkGet.class, PROJECT_ID)).thenReturn(response);

        final Collection<Obj> result = service.getObjsByUris(project, request.getItems());
        assertThat(result, is(response.getItems()));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetObjsByUrisWithClientSideHTTPError() {
        final BulkGetUris request = new BulkGetUris(Collections.singletonList(""));
        when(restTemplate.postForObject(BulkGet.URI, request, BulkGet.class, PROJECT_ID)).thenThrow(new RestClientException(""));
        service.getObjsByUris(project, request.getItems());
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetObjsByUrisWithServerSideHTTPError() {
        final BulkGetUris request = new BulkGetUris(Collections.singletonList(""));
        when(restTemplate.postForObject(BulkGet.URI, request, BulkGet.class, PROJECT_ID)).thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getObjsByUris(project, request.getItems());
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetObjsByUrisWithNoResponseFromAPI() {
        service.getObjsByUris(project, Collections.emptyList());
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByUriNullUri() {
        service.getObjByUri(null, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByUriNullCls() {
        service.getObjByUri(URI, null);
    }

    @Test(expectedExceptions = ObjNotFoundException.class)
    public void testGetObjByUriNotFound() {
        final GoodDataRestException restException = mock(GoodDataRestException.class);
        when(restException.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
        when(restTemplate.getForObject(URI, Obj.class)).thenThrow(restException);

        service.getObjByUri(URI, Obj.class);
    }

    @Test
    public void testGetObjByUri() {
        final Obj resultObj = mock(Obj.class);
        when(restTemplate.getForObject(URI, Obj.class)).thenReturn(resultObj);

        final Obj result = service.getObjByUri(URI, Obj.class);
        assertThat(result, is(resultObj));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetObjByUriWithClientSideHTTPError() {
        when(restTemplate.getForObject(URI, Obj.class)).thenThrow(new RestClientException(""));
        service.getObjByUri(URI, Queryable.class);
    }

    @Test(expectedExceptions = GoodDataRestException.class)
    public void testGetObjByUriWithServerSideHTTPError() {
        when(restTemplate.getForObject(URI, Obj.class)).thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getObjByUri(URI, Obj.class);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetObjByUriWithNoResponseFromAPI() {
        service.getObjByUri(URI, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByIdNullProject() {
        service.getObjById(null, ID, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByIdNullId() {
        service.getObjById(project, null, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByIdNullCls() {
        service.getObjById(project, ID, null);
    }

    @Test
    public void testGetObjById() {
        final Obj resultObj = mock(Obj.class);
        final String uri = format("/gdc/md/%s/obj/%s", PROJECT_ID, ID);
        when(restTemplate.getForObject(uri, Obj.class)).thenReturn(resultObj);

        final Obj result = service.getObjById(project, ID, Obj.class);
        assertThat(result, is(resultObj));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUsedByNullProject() {
        service.usedBy(null, URI, false, ReportDefinition.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindIdentifierUrisNullProject() {
        service.findUris(null, Restriction.identifier(ID));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindIdentifierUrisNullRestriction() {
        service.findUris(project, (Restriction) null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjUriNullProject() {
        service.getObjUri(null, Queryable.class, Restriction.identifier(""));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjUriNullClass() {
        service.getObjUri(project, null, Restriction.identifier(""));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetObjUriNoResponseFromAPI() {
        service.getObjUri(project, Queryable.class);
    }

    @Test
    public void testGetObjUriToFindOneObjByTitle() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String uri = "myURI";
        final String title = "myTitle";
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry));
        when(resultEntry.getTitle()).thenReturn(title);
        when(resultEntry.getUri()).thenReturn(uri);

        final String result = service.getObjUri(project, Queryable.class, Restriction.title(title));
        assertThat(result, is(uri));
    }

    @Test(expectedExceptions = NonUniqueObjException.class)
    public void testGetObjUriMoreThanOneResult() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));

        service.getObjUri(project, Queryable.class);
    }

    @Test(expectedExceptions = ObjNotFoundException.class)
    public void testGetObjUriNothingFound() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String title = "myTitle";
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry));

        service.getObjUri(project, Queryable.class, Restriction.title(title));
    }

    @Test
    public void testGetObjToFindOneObjById() {
        final Queryable intendedResult = mock(Queryable.class);
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String uri = "myURI";
        final String id = "myId";
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry));
        when(resultEntry.getIdentifier()).thenReturn(id);
        when(resultEntry.getUri()).thenReturn(uri);
        when(restTemplate.getForObject(uri, Queryable.class)).thenReturn(intendedResult);

        final Queryable result = service.getObj(project, Queryable.class, Restriction.identifier(id));
        assertThat(result, is(intendedResult));
    }

    @Test(expectedExceptions = NonUniqueObjException.class)
    public void testGetObjMoreThanOneResult() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));

        service.getObj(project, Queryable.class);
    }

    @Test(expectedExceptions = ObjNotFoundException.class)
    public void testGetObjNothingFound() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String title = "myTitle";
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry));

        service.getObj(project, Queryable.class, Restriction.title(title));
    }

    @Test
    public void testFindMoreResults() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));

        final Collection<Entry> results = service.find(project, Queryable.class);
        assertThat(results, allOf(hasItem(resultEntry1), hasItem(resultEntry2)));
    }

    @Test
    public void testFindIrregularQueryTypeNames() {
        when(restTemplate.getForObject(anyString(), any(), anyString(), anyString())).thenReturn(mock(Query.class));

        service.find(project, ReportDefinition.class);
        verify(restTemplate).getForObject(Query.URI, Query.class, project.getId(), "reportdefinition");

        service.find(project, VisualizationClass.class);
        verify(restTemplate).getForObject(Query.URI, Query.class, project.getId(), "visualizationclasses");
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testFindWithWithClientSideHTTPError() {
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenThrow(new RestClientException(""));
        service.find(project, Queryable.class);
    }

    @Test
    public void testFindUrisBySummary() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);
        final String summary = "mySummary";
        final String uri1 = "uri1";
        final String uri2 = "uri2";
        when(restTemplate.getForObject(Query.URI, Query.class, project.getId(), "queryables")).thenReturn(queryResult);
        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));
        when(resultEntry1.getSummary()).thenReturn(summary);
        when(resultEntry2.getSummary()).thenReturn(summary);
        when(resultEntry1.getUri()).thenReturn(uri1);
        when(resultEntry2.getUri()).thenReturn(uri2);

        final Collection<String> results = service.findUris(project, Queryable.class, Restriction.summary(summary));
        assertThat(results, allOf(hasItem(uri1), hasItem(uri2)));
    }

    @Test
    public void testGetAttributeElementsEmpty() {
        final DisplayForm attrDisplayForm = mock(AttributeDisplayForm.class);
        when(attrDisplayForm.getElementsUri()).thenReturn("elementsUri");
        final Attribute attr = mock(Attribute.class);
        when(attr.getDefaultDisplayForm()).thenReturn(attrDisplayForm);

        when(restTemplate.getForObject("elementsUri", AttributeElements.class))
                .thenReturn(readObjectFromResource("/md/attributeElements-empty.json", AttributeElements.class));
        final List<AttributeElement> elements = service.getAttributeElements(attr);
        assertThat(elements, hasSize(0));
    }

    @Test
    public void testGetAttributeElements() {
        final DisplayForm attrDisplayForm = mock(AttributeDisplayForm.class);
        when(attrDisplayForm.getElementsUri()).thenReturn("elementsUri");
        final Attribute attr = mock(Attribute.class);
        when(attr.getDefaultDisplayForm()).thenReturn(attrDisplayForm);

        final AttributeElement result1 = mock(AttributeElement.class);
        final AttributeElement result2 = mock(AttributeElement.class);
        final AttributeElements attrElements = readObjectFromResource("/md/attributeElements-empty.json",
                AttributeElements.class);
        attrElements.getElements().addAll(asList(result1, result2));

        when(restTemplate.getForObject("elementsUri", AttributeElements.class)).thenReturn(attrElements);
        final List<AttributeElement> elements = service.getAttributeElements(attr);
        assertThat(elements, allOf(hasItem(result1), hasItem(result2)));
    }

}
