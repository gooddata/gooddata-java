/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.DataStoreException;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DatasetServiceTest {

    private static final String PROJECT_ID = "17";
    private static final String DATASET_ID = "11";

    @Mock
    private RestTemplate restTemplate;
    @Mock
    private DataStoreService dataStoreService;
    @Mock
    private Project project;
    @Mock
    private Dataset dataset;
    @Mock
    private DatasetManifest manifest;
    @Mock
    private InputStream stream;

    private DatasetService service;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new DatasetService(restTemplate, dataStoreService);
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetDatasetManifestWithNullProject() throws Exception {
        service.getDatasetManifest(null, DATASET_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetDatasetManifestWithNullId() throws Exception {
        service.getDatasetManifest(project, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetDatasetManifestWithEmptyId() throws Exception {
        service.getDatasetManifest(project, "");
    }

    @Test
    public void testGetDatasetManifest() throws Exception {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenReturn(manifest);
        final DatasetManifest result = service.getDatasetManifest(project, DATASET_ID);

        assertThat(result, is(manifest));
    }

    @Test(expectedExceptions = DatasetNotFoundException.class)
    public void testGetDatasetManifestWhenNotFound() throws Exception {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenThrow(new GoodDataRestException(404, "", "", "", ""));
        service.getDatasetManifest(project, DATASET_ID);
    }

    @Test(expectedExceptions = DatasetException.class)
    public void testGetDatasetManifestWhenRestClientError() throws Exception {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenThrow(new RestClientException(""));
        service.getDatasetManifest(project, DATASET_ID);
    }

    @Test(expectedExceptions = DatasetException.class)
    public void testGetDatasetManifestWhenOtherError() throws Exception {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getDatasetManifest(project, DATASET_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetWithNullProject() throws Exception {
        service.loadDataset(null, manifest, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetWithNullManifest() throws Exception {
        final DatasetManifest nullManifest = null;
        service.loadDataset(project, nullManifest, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetWithNullDataset() throws Exception {
        service.loadDataset(project, manifest, null);
    }

    @Test(expectedExceptions = DatasetException.class)
    public void testLoadDatasetWhenUploadFails() throws Exception {
        doThrow(DataStoreException.class).when(dataStoreService).upload(anyString(), eq(stream));
        when(manifest.getFile()).thenReturn("");
        service.loadDataset(project, manifest, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithNullProject() throws Exception {
        service.loadDataset(null, DATASET_ID, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithNullId() throws Exception {
        final String nullId = null;
        service.loadDataset(project, nullId, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithEmptyId() throws Exception {
        service.loadDataset(project, "", stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithNullInputStream() throws Exception {
        service.loadDataset(project, DATASET_ID, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testListDatasetsWithNullProject() throws Exception {
        service.listDatasets(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetsWithNullResponse() throws Exception {
        when(restTemplate.getForObject(Datasets.URI, Dataset.class, PROJECT_ID)).thenReturn(null);
        service.listDatasets(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetsWithRestClientError() throws Exception {
        when(restTemplate.getForObject(Datasets.URI, Dataset.class, PROJECT_ID)).thenThrow(new RestClientException(""));
        service.listDatasets(project);
    }

    @Test
    public void testListDatasetsWithEmptyResponse() throws Exception {
        final Datasets datasets = mock(Datasets.class);
        when(restTemplate.getForObject(Datasets.URI, Datasets.class, PROJECT_ID)).thenReturn(datasets);
        when(datasets.getLinks()).thenReturn(asList(dataset));

        final Collection<Dataset> result = service.listDatasets(project);
        assertThat(result, hasSize(1));
        assertThat(result, contains(dataset));
    }
}