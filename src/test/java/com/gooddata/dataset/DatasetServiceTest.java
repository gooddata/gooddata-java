/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.dataset;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.gdc.AboutLinks.Link;
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
import static org.hamcrest.CoreMatchers.notNullValue;
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
    private Link datasetLink;
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
        when(manifest.getSource()).thenReturn(stream);
        when(manifest.getDataSet()).thenReturn(DATASET_ID);
        service.loadDatasets(project, manifest);
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

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testListDatasetsWithNullProject() throws Exception {
        service.listDatasets(null);
    }

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetsWithNullResponse() throws Exception {
        when(restTemplate.getForObject(Datasets.URI, Link.class, PROJECT_ID)).thenReturn(null);
        service.listDatasets(project);
    }

    @SuppressWarnings("deprecation")
    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetsWithRestClientError() throws Exception {
        when(restTemplate.getForObject(Datasets.URI, Link.class, PROJECT_ID)).thenThrow(new RestClientException(""));
        service.listDatasets(project);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testListDatasetsWithEmptyResponse() throws Exception {
        final DatasetLinks datasets = mock(DatasetLinks.class);
        when(restTemplate.getForObject(DatasetLinks.URI, DatasetLinks.class, PROJECT_ID)).thenReturn(datasets);
        when(datasets.getLinks()).thenReturn(singletonList(datasetLink));
        when(datasetLink.getIdentifier()).thenReturn("ID");

        final Collection<Dataset> result = service.listDatasets(project);
        assertThat(result, hasSize(1));
        final Dataset dataset = result.iterator().next();
        assertThat(dataset, is(notNullValue()));
        assertThat(dataset.getIdentifier(), is("ID"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testListDatasetLinksWithNullProject() throws Exception {
        service.listDatasetLinks(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetLinksWithNullResponse() throws Exception {
        when(restTemplate.getForObject(DatasetLinks.URI, Link.class, PROJECT_ID)).thenReturn(null);
        service.listDatasetLinks(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetLinksWithRestClientError() throws Exception {
        when(restTemplate.getForObject(DatasetLinks.URI, Link.class, PROJECT_ID)).thenThrow(new RestClientException(""));
        service.listDatasetLinks(project);
    }

    @Test
    public void testListDatasetLinksWithEmptyResponse() throws Exception {
        final DatasetLinks datasets = mock(DatasetLinks.class);
        when(restTemplate.getForObject(DatasetLinks.URI, DatasetLinks.class, PROJECT_ID)).thenReturn(datasets);
        when(datasets.getLinks()).thenReturn(singletonList(datasetLink));

        final Collection<Link> result = service.listDatasetLinks(project);
        assertThat(result, hasSize(1));
        assertThat(result, contains(datasetLink));
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

        assertThat(service.listUploadsForDataset(project, DATASET_ID), Matchers.empty());
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

        assertThat(service.listUploadsForDataset(project, DATASET_ID), Matchers.empty());
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