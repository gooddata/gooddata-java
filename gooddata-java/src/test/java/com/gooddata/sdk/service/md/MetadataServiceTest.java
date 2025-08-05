/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.md;

import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.gdc.UriResponse;
import com.gooddata.sdk.model.md.*;
import com.gooddata.sdk.model.md.report.ReportDefinition;
import com.gooddata.sdk.model.md.visualization.VisualizationClass;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test; 
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;


import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import static com.gooddata.sdk.common.util.ResourceUtils.readObjectFromResource;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.function.Function;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("rawtypes")
public class MetadataServiceTest {

    private static final String URI = "TEST_URI";
    private static final String ID = "TEST_ID";
    private static final String PROJECT_ID = "TEST_PROJ_ID";

    @Mock
    private Project project;
    @Mock
    private WebClient webClient;

    private MetadataService service;

    @BeforeEach
    public void setUp() throws Exception {
        service = new MetadataService(webClient, new GoodDataSettings()); 
        lenient().when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test
    void testCreateObjNullResponse() {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class); 
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class); 
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class); 

        Obj obj = mock(Obj.class);

        // changed: full WebClient chain for POST
        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object[]) any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UriResponse.class)).thenReturn(Mono.empty());

        // changed: JUnit 5 assertThrows
        assertThrows(ObjCreateException.class, () -> service.createObj(project, obj));
    }

    @Test
    void testCreateObjGDRestException() {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Obj obj = mock(Obj.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object[]) any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UriResponse.class)).thenReturn(Mono.error(new GoodDataRestException(500, "", "", "", "")));

        assertThrows(ObjCreateException.class, () -> service.createObj(project, obj));
    }

    @Test
    void testCreateObjRestClientException() {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        Obj obj = mock(Obj.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString(), (Object[]) any())).thenReturn(bodySpec);
        when(bodySpec.bodyValue(any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UriResponse.class)).thenReturn(Mono.error(new RuntimeException("rest error")));

        assertThrows(ObjCreateException.class, () -> service.createObj(project, obj));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testCreateObj() {

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        final Obj obj = mock(Obj.class);
        final Obj resultObj = mock(Obj.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(eq(obj))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);


        when(responseSpec.bodyToMono(any(Class.class))).thenReturn(Mono.just(resultObj));

        final Obj result = service.createObj(project, obj);
        assertThat(result, is(notNullValue()));
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    void testUpdateObj() {
        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class); 
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class); 

        final Updatable obj = mock(Updatable.class);
        final Updatable resultObj = mock(Updatable.class);

        when(obj.getUri()).thenReturn(URI);

        // Mock WebClient chain for PUT
        when(webClient.put()).thenReturn(uriSpec); 
        when(uriSpec.uri(eq(URI))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(eq(obj))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity())
            .thenReturn(Mono.just(ResponseEntity.ok().build()));

        // Mock WebClient chain for GET (getObjByUri)
        WebClient.RequestHeadersUriSpec getUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec getHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec getResponseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(getUriSpec);
        when(getUriSpec.uri(eq(URI))).thenReturn(getHeadersSpec);
        when(getHeadersSpec.retrieve()).thenReturn(getResponseSpec);
        when(getResponseSpec.bodyToMono((Class<Updatable>) any())).thenReturn(Mono.just(resultObj));
        final Obj result = service.updateObj(obj);
        assertThat(result, is(notNullValue()));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testUpdateObjNotFound() {
        final Updatable obj = mock(Updatable.class);
        when(obj.getUri()).thenReturn(URI);

        assertThrows(ObjUpdateException.class, () -> service.updateObj(obj));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testUpdateObjRestException() {
        final Updatable obj = mock(Updatable.class);
        when(obj.getUri()).thenReturn(URI);


        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.put()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(eq(obj))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        lenient().when(responseSpec.bodyToMono(obj.getClass())).thenReturn(Mono.error(new RuntimeException("RestClientException")));

        assertThrows(ObjUpdateException.class, () -> service.updateObj(obj));
    }

    @Test
    void testGetObjsByUrisNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjsByUris(null, Collections.emptyList()));
    }

    @Test
    void testGetObjsByUrisNullUris() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjsByUris(project, null));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjsByUris() {
        final BulkGetUris request = new BulkGetUris(singletonList(URI));
        final BulkGet response = readObjectFromResource("/md/bulk-get.json", BulkGet.class);

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(eq(request))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BulkGet.class)).thenReturn(Mono.just(response));

        final Collection<Obj> result = service.getObjsByUris(project, request.getItems());
        assertThat(result, is(response.getItems()));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjsByUrisWithClientSideHTTPError() {
        final BulkGetUris request = new BulkGetUris(singletonList(""));

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(BulkGet.URI), eq(PROJECT_ID))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(eq(request))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BulkGet.class)).thenReturn(Mono.error(new RuntimeException("client error")));

        assertThrows(GoodDataException.class, () -> service.getObjsByUris(project, request.getItems()));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjsByUrisWithServerSideHTTPError() {
        final BulkGetUris request = new BulkGetUris(singletonList(""));

        WebClient.RequestBodyUriSpec uriSpec = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = mock(WebClient.RequestBodySpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(bodySpec);
        when(bodySpec.bodyValue(eq(request))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(BulkGet.class)).thenReturn(
                Mono.error(new GoodDataRestException(500, "", "", "", "")));

        assertThrows(GoodDataRestException.class, () -> service.getObjsByUris(project, request.getItems()));
    }

    @Test
    void testGetObjsByUrisWithNoResponseFromAPI() {
        assertThrows(GoodDataException.class, () -> service.getObjsByUris(project, Collections.emptyList()));
    }

    @Test
    void testGetObjByUriNullUri() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjByUri(null, Obj.class));
    }

    @Test
    void testGetObjByUriNullCls() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjByUri(URI, null));
    }

    @Test
    void testGetObjByUriNotFound() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Obj.class)).thenReturn(Mono.error(new GoodDataRestException(404, "", "", "", "")));

        assertThrows(ObjNotFoundException.class, () -> service.getObjByUri(URI, Obj.class));
    }

    @Test
    void testGetObjByUri() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Obj resultObj = mock(Obj.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Obj.class)).thenReturn(Mono.just(resultObj));

        Obj result = service.getObjByUri(URI, Obj.class);
        assertThat(result, is(resultObj));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjByUriWithClientSideHTTPError() {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Queryable.class)).thenReturn(Mono.error(new RuntimeException("RestClientException")));

        assertThrows(GoodDataException.class, () -> service.getObjByUri(URI, Queryable.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjByUriWithServerSideHTTPError() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class); 
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        GoodDataRestException restException = new GoodDataRestException(500, "", "", "", "");
        when(responseSpec.bodyToMono(Obj.class)).thenReturn(Mono.error(restException));

        GoodDataException ex = assertThrows(GoodDataException.class, () -> service.getObjByUri(URI, Obj.class));
        assertThat(ex.getCause(), is(restException));
    }


    @Test
    void testGetObjByUriWithNoResponseFromAPI() {
        assertThrows(GoodDataException.class, () -> service.getObjByUri(URI, Obj.class));
    }

    @Test
    void testGetObjByIdNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjById(null, ID, Obj.class));
    }

    @Test
    void testGetObjByIdNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjById(project, null, Obj.class));
    }

    @Test
    void testGetObjByIdNullCls() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjById(project, ID, null));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjById() {
        final Obj resultObj = mock(Obj.class);
        final String uri = format("/gdc/md/%s/obj/%s", PROJECT_ID, ID);


        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(uri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Obj.class)).thenReturn(Mono.just(resultObj));

        final Obj result = service.getObjById(project, ID, Obj.class);
        assertThat(result, is(resultObj));
    }

    @Test
    void testUsedByNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.usedBy(null, URI, false, ReportDefinition.class));
    }

    @Test
    void testFindIdentifierUrisNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.findUris(null, Restriction.identifier(ID)));
    }

    @Test
    void testFindIdentifierUrisNullRestriction() {
        assertThrows(IllegalArgumentException.class, () -> service.findUris(project, (Restriction) null));
    }

    @Test
    void testGetObjUriNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjUri(null, Queryable.class, Restriction.identifier("")));
    }

    @Test
    void testGetObjUriNullClass() {
        assertThrows(IllegalArgumentException.class, () -> service.getObjUri(project, null, Restriction.identifier("")));
    }

    @Test
    void testGetObjUriNoResponseFromAPI() {
        assertThrows(GoodDataException.class, () -> service.getObjUri(project, Queryable.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjUriToFindOneObjByTitle() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String uri = "myURI";
        final String title = "myTitle";


        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(singletonList(resultEntry));
        when(resultEntry.getTitle()).thenReturn(title);
        when(resultEntry.getUri()).thenReturn(uri);

        final String result = service.getObjUri(project, Queryable.class, Restriction.title(title));
        assertThat(result, is(uri));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjUriMoreThanOneResult() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);


        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));

        assertThrows(NonUniqueObjException.class, () -> service.getObjUri(project, Queryable.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjUriNothingFound() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String title = "myTitle";

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));
        when(queryResult.getEntries()).thenReturn(singletonList(resultEntry));

        assertThrows(ObjNotFoundException.class, () -> service.getObjUri(project, Queryable.class, Restriction.title(title)));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjToFindOneObjById() {
        final Queryable intendedResult = mock(Queryable.class);
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String uri = "myURI";
        final String id = "myId";


        WebClient.RequestHeadersUriSpec uriSpec1 = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec1 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec1 = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec1);
        when(uriSpec1.uri(any(Function.class))).thenReturn(headersSpec1);
        when(headersSpec1.retrieve()).thenReturn(responseSpec1);
        when(responseSpec1.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(singletonList(resultEntry));
        when(resultEntry.getIdentifier()).thenReturn(id);
        when(resultEntry.getUri()).thenReturn(uri);

        WebClient.RequestHeadersUriSpec uriSpec2 = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec2 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec1).thenReturn(uriSpec2);
        when(uriSpec2.uri(eq(uri))).thenReturn(headersSpec2);
        when(headersSpec2.retrieve()).thenReturn(responseSpec2);
        when(responseSpec2.bodyToMono(Queryable.class)).thenReturn(Mono.just(intendedResult));

        final Queryable result = service.getObj(project, Queryable.class, Restriction.identifier(id));
        assertThat(result, is(intendedResult));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjMoreThanOneResult() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));

        assertThrows(NonUniqueObjException.class, () -> service.getObj(project, Queryable.class));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetObjNothingFound() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry = mock(Entry.class);
        final String title = "myTitle";

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(singletonList(resultEntry));

        assertThrows(ObjNotFoundException.class, () -> service.getObj(project, Queryable.class, Restriction.title(title)));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testFindMoreResults() {
        final Query queryResult = mock(Query.class);
        final Entry resultEntry1 = mock(Entry.class);
        final Entry resultEntry2 = mock(Entry.class);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(asList(resultEntry1, resultEntry2));

        final Collection<Entry> results = service.find(project, Queryable.class);
        assertThat(results, allOf(hasItem(resultEntry1), hasItem(resultEntry2)));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testFindIrregularQueryTypeNames() {
        when(project.getId()).thenReturn(PROJECT_ID);

        // Mock chain for ReportDefinition (reportdefinition)
        WebClient.RequestHeadersUriSpec uriSpec1 = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec1 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec1 = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec1);
        when(uriSpec1.uri(any(Function.class))).thenReturn(headersSpec1);
        when(headersSpec1.retrieve()).thenReturn(responseSpec1);
        when(responseSpec1.bodyToMono(Query.class)).thenReturn(Mono.just(mock(Query.class)));

        service.find(project, ReportDefinition.class);

        verify(webClient).get();
        verify(uriSpec1).uri(any(Function.class));

        // Mock chain for VisualizationClass (visualizationclasses)
        WebClient.RequestHeadersUriSpec uriSpec2 = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec2 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec2 = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec2);
        when(uriSpec2.uri(any(Function.class))).thenReturn(headersSpec2);
        when(headersSpec2.retrieve()).thenReturn(responseSpec2);
        when(responseSpec2.bodyToMono(Query.class)).thenReturn(Mono.just(mock(Query.class)));

        service.find(project, VisualizationClass.class);

        verify(webClient, times(2)).get();
        verify(uriSpec2).uri(any(Function.class));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testFindWithWithClientSideHTTPError() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(Query.URI), eq(PROJECT_ID), eq("queryables"))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.error(new RuntimeException("RestClientException")));

        assertThrows(GoodDataException.class, () -> service.find(project, Queryable.class));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testFindUrisBySummary() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);
        Project project = mock(Project.class);
        Query queryResult = mock(Query.class);
        Entry resultEntry1 = mock(Entry.class);
        Entry resultEntry2 = mock(Entry.class);

        String summary = "mySummary";
        String uri1 = "uri1";
        String uri2 = "uri2";

        when(project.getId()).thenReturn("TEST_PROJ_ID");

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec); // <--- ВАЖНО!
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Query.class)).thenReturn(Mono.just(queryResult));

        when(queryResult.getEntries()).thenReturn(Arrays.asList(resultEntry1, resultEntry2));
        when(resultEntry1.getSummary()).thenReturn(summary);
        when(resultEntry2.getSummary()).thenReturn(summary);
        when(resultEntry1.getUri()).thenReturn(uri1);
        when(resultEntry2.getUri()).thenReturn(uri2);

        Collection<String> results = service.findUris(project, Queryable.class, Restriction.summary(summary));
        assertThat(results, allOf(hasItem(uri1), hasItem(uri2)));
    }



    @SuppressWarnings("rawtypes")
    @Test
    void testGetAttributeElementsEmpty() {
        final DisplayForm attrDisplayForm = mock(DisplayForm.class);
        when(attrDisplayForm.getElementsUri()).thenReturn("elementsUri");
        final Attribute attr = mock(Attribute.class);
        when(attr.getDefaultDisplayForm()).thenReturn(attrDisplayForm);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        AttributeElements emptyElements = readObjectFromResource("/md/attributeElements-empty.json", AttributeElements.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("elementsUri"))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AttributeElements.class)).thenReturn(Mono.just(emptyElements));

        final List<AttributeElement> elements = service.getAttributeElements(attr);
        assertThat(elements, hasSize(0));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetAttributeElements() {
        final DisplayForm attrDisplayForm = mock(DisplayForm.class);
        when(attrDisplayForm.getElementsUri()).thenReturn("elementsUri");
        final Attribute attr = mock(Attribute.class);
        when(attr.getDefaultDisplayForm()).thenReturn(attrDisplayForm);

        final AttributeElement result1 = mock(AttributeElement.class);
        final AttributeElement result2 = mock(AttributeElement.class);
        final AttributeElements attrElements = readObjectFromResource("/md/attributeElements-empty.json", AttributeElements.class);
        attrElements.getElements().addAll(Arrays.asList(result1, result2));

        // Мокаем цепочку WebClient
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq("elementsUri"))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(AttributeElements.class)).thenReturn(Mono.just(attrElements));

        final List<AttributeElement> elements = service.getAttributeElements(attr);
        assertThat(elements, allOf(hasItem(result1), hasItem(result2)));
    }

    @Test
    void testGetTimezoneNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.getTimezone(null));
    }

    @Test
    void testGetTimezoneNullProjectId() {
        assertThrows(IllegalArgumentException.class, () -> service.getTimezone(mock(Project.class)));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetTimezone() {

        Service serviceResult = readObjectFromResource("/md/service-timezone.json", Service.class);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);


        when(project.getId()).thenReturn(PROJECT_ID);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(any(Function.class))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Service.class)).thenReturn(Mono.just(serviceResult));

        final String tz = service.getTimezone(project);
        assertThat(tz, is("UTC"));
    }


    @Test
    void testSetTimezoneNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.setTimezone(null, "test"));
    }

    @Test
    void testSetTimezoneNullTZ() {
        assertThrows(IllegalArgumentException.class, () -> service.setTimezone(project, null));
    }

    @Test
    void testSetTimezoneEmptyTZ() {
        assertThrows(IllegalArgumentException.class, () -> service.setTimezone(project, ""));
    }

}
