/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.md;

import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.UriResponse;
import com.gooddata.project.Project;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        service = new MetadataService(restTemplate);
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test(expectedExceptions = ObjCreateException.class)
    public void testCreateObjNullResponse() throws Exception {
        final Obj obj = mock(Obj.class);
        service.createObj(project, obj);
    }

    @Test(expectedExceptions = ObjCreateException.class)
    @SuppressWarnings("unchecked")
    public void testCreateObjGDRestException() throws Exception {
        final Obj obj = mock(Obj.class);
        when(restTemplate.postForObject(Obj.URI, obj, UriResponse.class, PROJECT_ID))
                .thenThrow(GoodDataRestException.class);
        service.createObj(project, obj);
    }

    @Test(expectedExceptions = ObjCreateException.class)
    public void testCreateObjRestClientException() throws Exception {
        final Obj obj = mock(Obj.class);
        when(restTemplate.postForObject(Obj.URI, obj, UriResponse.class, PROJECT_ID))
                .thenThrow(new RestClientException(""));
        service.createObj(project, obj);
    }

    @Test
    public void testCreateObj() throws Exception {
        final Obj obj = mock(Obj.class);
        final Obj resultObj = mock(Obj.class);
        final UriResponse uriResp = mock(UriResponse.class);

        when(restTemplate.postForObject(Obj.URI, obj, UriResponse.class, PROJECT_ID)).thenReturn(uriResp);
        when(uriResp.getUri()).thenReturn(URI);
        when(restTemplate.getForObject(URI, obj.getClass())).thenReturn(resultObj);

        final Obj result = service.createObj(project, obj);
        assertThat(result, is(notNullValue()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByUriNullUri() throws Exception {
        service.getObjByUri(null, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByUriNullCls() throws Exception {
        service.getObjByUri(URI, null);
    }

    @Test(expectedExceptions = ObjNotFoundException.class)
    public void testGetObjByUriNotFound() throws Exception {
        final GoodDataRestException restException = mock(GoodDataRestException.class);
        when(restException.getStatusCode()).thenReturn(HttpStatus.NOT_FOUND.value());
        when(restTemplate.getForObject(URI, Obj.class)).thenThrow(restException);

        service.getObjByUri(URI, Obj.class);
    }

    @Test
    public void testGetObjByUri() throws Exception {
        final Obj resultObj = mock(Obj.class);
        when(restTemplate.getForObject(URI, resultObj.getClass())).thenReturn(resultObj);

        final Obj result = service.getObjByUri(URI, resultObj.getClass());
        assertThat(result, is(resultObj));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByIdNullProject() throws Exception {
        service.getObjById(null, ID, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByIdNullId() throws Exception {
        service.getObjById(project, null, Obj.class);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetObjByIdNullCls() throws Exception {
        service.getObjById(project, ID, null);
    }

    @Test
    public void testGetObjById() throws Exception {
        final Obj resultObj = mock(Obj.class);
        final String uri = format("/gdc/md/%s/obj/%s", PROJECT_ID, ID);
        when(restTemplate.getForObject(uri, resultObj.getClass())).thenReturn(resultObj);

        final Obj result = service.getObjById(project, ID, resultObj.getClass());
        assertThat(result, is(resultObj));
    }

//TODO the other methods

}