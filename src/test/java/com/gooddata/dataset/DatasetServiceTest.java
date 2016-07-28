/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.dataset;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.DataStoreException;
import com.gooddata.gdc.DataStoreService;
import com.gooddata.project.Project;
import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.InputStream;
import java.util.Collection;

import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
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
    private static final String DATASET_UPLOADS_URI = "uploads/uri";

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
        when(datasets.getLinks()).thenReturn(singletonList(dataset));

        final Collection<Dataset> result = service.listDatasets(project);
        assertThat(result, hasSize(1));
        assertThat(result, contains(dataset));
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetDataSetInfoRestClientError() throws Exception {
        when(restTemplate.getForObject(UploadsInfo.URI, UploadsInfo.class, PROJECT_ID))
                .thenThrow(new RestClientException(""));
        service.getDataSetInfo(project, "dataset.id");
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetDataSetInfoEmptyResponse() throws Exception {
        when(restTemplate.getForObject(UploadsInfo.URI, UploadsInfo.class, PROJECT_ID)).thenReturn(null);
        service.getDataSetInfo(project, "dataset.id");
    }

    @Test
    public void testListUploadsForDatasetMissingUri() throws Exception {
        mockDataSetInfo();

        assertThat(service.listUploadsForDataset(project, DATASET_ID), Matchers.<Upload>empty());
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUploadsForDatasetRestClientError() throws Exception {
        final UploadsInfo.DataSet dataSetInfo = mockDataSetInfo();
        when(dataSetInfo.getUploadsUri()).thenReturn(DATASET_UPLOADS_URI);

        when(restTemplate.getForObject(DATASET_UPLOADS_URI, Uploads.class)).thenThrow(new RestClientException(""));

        service.listUploadsForDataset(project, DATASET_ID);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUploadsForDatasetEmptyResponse() throws Exception {
        final UploadsInfo.DataSet dataSetInfo = mockDataSetInfo();
        when(dataSetInfo.getUploadsUri()).thenReturn(DATASET_UPLOADS_URI);

        when(restTemplate.getForObject(DATASET_UPLOADS_URI, Uploads.class)).thenReturn(null);

        service.listUploadsForDataset(project, DATASET_ID);
    }

    @Test
    public void testListUploadsForDatasetEmptyResponseBody() throws Exception {
        final UploadsInfo.DataSet dataSetInfo = mockDataSetInfo();
        when(dataSetInfo.getUploadsUri()).thenReturn(DATASET_UPLOADS_URI);

        when(restTemplate.getForObject(DATASET_UPLOADS_URI, Uploads.class)).thenReturn(new Uploads(null));

        assertThat(service.listUploadsForDataset(project, DATASET_ID), Matchers.<Upload>empty());
    }

    @Test
    public void testGetLastUploadForDatasetMissingUri() throws Exception {
        mockDataSetInfo();

        assertThat(service.getLastUploadForDataset(project, DATASET_ID), nullValue());
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetLastUploadForDatasetRestClientError() throws Exception {
        final UploadsInfo.DataSet dataSetInfo = mockDataSetInfo();
        final String lastUploadUri = "last/upload/uri";
        when(dataSetInfo.getLastUploadUri()).thenReturn(lastUploadUri);

        when(restTemplate.getForObject(lastUploadUri, Upload.class)).thenThrow(new RestClientException(""));

        service.getLastUploadForDataset(project, DATASET_ID);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetUploadStatisticsWhenRestClientError() throws Exception {
        when(restTemplate.getForObject(UploadStatistics.URI, UploadStatistics.class, PROJECT_ID))
                .thenThrow(new RestClientException(""));

        service.getUploadStatistics(project);
    }

    private UploadsInfo.DataSet mockDataSetInfo() {
        final UploadsInfo.DataSet dataSetInfo = mock(UploadsInfo.DataSet.class);
        when(dataSetInfo.getDatasetId()).thenReturn(DATASET_ID);

        final UploadsInfo uploadsInfo = new UploadsInfo(singletonList(dataSetInfo));

        when(restTemplate.getForObject(UploadsInfo.URI_TEMPLATE.expand(PROJECT_ID), UploadsInfo.class))
                .thenReturn(uploadsInfo);

        return dataSetInfo;
    }
}