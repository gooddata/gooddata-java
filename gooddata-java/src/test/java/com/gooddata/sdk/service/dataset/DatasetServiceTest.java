/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataset;

import com.gooddata.GoodDataException;
import com.gooddata.GoodDataRestException;
import com.gooddata.sdk.model.dataset.*;
import com.gooddata.sdk.model.gdc.AboutLinks.Link;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.gdc.DataStoreException;
import com.gooddata.sdk.service.gdc.DataStoreService;
import org.hamcrest.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static com.gooddata.util.ResourceUtils.OBJECT_MAPPER;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

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
    private DatasetManifest manifest;
    @Mock
    private InputStream stream;

    private DatasetService service;
    private static final String DATASET_UPLOADS_URI = "uploads/uri";

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        service = new DatasetService(restTemplate, dataStoreService, new GoodDataSettings());
        when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetDatasetManifestWithNullProject() {
        service.getDatasetManifest(null, DATASET_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetDatasetManifestWithNullId() {
        service.getDatasetManifest(project, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetDatasetManifestWithEmptyId() {
        service.getDatasetManifest(project, "");
    }

    @Test(expectedExceptions = DatasetNotFoundException.class)
    public void testGetDatasetManifestWhenNotFound() {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenThrow(new GoodDataRestException(404, "", "", "", ""));
        service.getDatasetManifest(project, DATASET_ID);
    }

    @Test(expectedExceptions = DatasetException.class)
    public void testGetDatasetManifestWhenRestClientError() {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenThrow(new RestClientException(""));
        service.getDatasetManifest(project, DATASET_ID);
    }

    @Test(expectedExceptions = DatasetException.class)
    public void testGetDatasetManifestWhenOtherError() {
        when(restTemplate.getForObject(DatasetManifest.URI, DatasetManifest.class, PROJECT_ID, DATASET_ID))
                .thenThrow(new GoodDataRestException(500, "", "", "", ""));
        service.getDatasetManifest(project, DATASET_ID);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetWithNullProject() {
        service.loadDataset(null, manifest, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetWithNullManifest() {
        service.loadDataset(project, (DatasetManifest) null, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetWithNullDataset() {
        service.loadDataset(project, manifest, null);
    }

    @Test(expectedExceptions = DatasetException.class)
    public void testLoadDatasetWhenUploadFails() {
        doThrow(DataStoreException.class).when(dataStoreService).upload(anyString(), eq(stream));
        when(manifest.getFile()).thenReturn("");
        when(manifest.getSource()).thenReturn(stream);
        when(manifest.getDataSet()).thenReturn(DATASET_ID);
        service.loadDatasets(project, manifest);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithNullProject() {
        service.loadDataset(null, DATASET_ID, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithNullId() {
        service.loadDataset(project, (String) null, stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithEmptyId() {
        service.loadDataset(project, "", stream);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testLoadDatasetByIdWithNullInputStream() {
        service.loadDataset(project, DATASET_ID, null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testListDatasetLinksWithNullProject() {
        service.listDatasetLinks(null);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetLinksWithNullResponse() {
        when(restTemplate.getForObject(DatasetLinks.URI, Link.class, PROJECT_ID)).thenReturn(null);
        service.listDatasetLinks(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListDatasetLinksWithRestClientError() {
        when(restTemplate.getForObject(DatasetLinks.URI, Link.class, PROJECT_ID)).thenThrow(new RestClientException(""));
        service.listDatasetLinks(project);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetDataSetInfoRestClientError() {
        when(restTemplate.getForObject(UploadsInfo.URI, UploadsInfo.class, PROJECT_ID))
                .thenThrow(new RestClientException(""));
        service.getDataSetInfo(project, "dataset.id");
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetDataSetInfoEmptyResponse() {
        when(restTemplate.getForObject(UploadsInfo.URI, UploadsInfo.class, PROJECT_ID)).thenReturn(null);
        service.getDataSetInfo(project, "dataset.id");
    }

    @Test
    public void testListUploadsForDatasetMissingUri() throws Exception {
        mockDataSetInfo(null, null);

        assertThat(service.listUploadsForDataset(project, DATASET_ID), Matchers.empty());
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUploadsForDatasetRestClientError() throws Exception {
        mockDataSetInfo(DATASET_UPLOADS_URI, null);

        when(restTemplate.getForObject(DATASET_UPLOADS_URI, Uploads.class)).thenThrow(new RestClientException(""));

        service.listUploadsForDataset(project, DATASET_ID);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testListUploadsForDatasetEmptyResponse() throws Exception {
        mockDataSetInfo(DATASET_UPLOADS_URI, null);

        when(restTemplate.getForObject(DATASET_UPLOADS_URI, Uploads.class)).thenReturn(null);

        service.listUploadsForDataset(project, DATASET_ID);
    }

    @Test
    public void testListUploadsForDatasetEmptyResponseBody() throws Exception {
        mockDataSetInfo(DATASET_UPLOADS_URI, null);

        when(restTemplate.getForObject(DATASET_UPLOADS_URI, Uploads.class))
                .thenReturn(OBJECT_MAPPER.readValue("{\"dataUploads\":{}}", Uploads.class));

        assertThat(service.listUploadsForDataset(project, DATASET_ID), Matchers.empty());
    }

    @Test
    public void testGetLastUploadForDatasetMissingUri() throws Exception {
        mockDataSetInfo(DATASET_UPLOADS_URI, null);

        assertThat(service.getLastUploadForDataset(project, DATASET_ID), nullValue());
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetLastUploadForDatasetRestClientError() throws Exception {
        final String lastUploadUri = "last/upload/uri";
        mockDataSetInfo(DATASET_UPLOADS_URI, lastUploadUri);

        when(restTemplate.getForObject(lastUploadUri, Upload.class)).thenThrow(new RestClientException(""));

        service.getLastUploadForDataset(project, DATASET_ID);
    }

    @Test(expectedExceptions = GoodDataException.class)
    public void testGetUploadStatisticsWhenRestClientError() {
        when(restTemplate.getForObject(UploadStatistics.URI, UploadStatistics.class, PROJECT_ID))
                .thenThrow(new RestClientException(""));

        service.getUploadStatistics(project);
    }

    private void mockDataSetInfo(final String dataUploadsUri, final String lastUploadUri) throws IOException {
        String dataUploadsPart = "";
        if (dataUploadsUri != null) {
            dataUploadsPart = format(",\"dataUploads\":\"%s\"", dataUploadsUri);
        }

        String lastUploadUriPart = "";
        if (lastUploadUri != null) {
            lastUploadUriPart = format("\"uri\":\"%s\"", lastUploadUri);
        }

        final String LAST_UPLOAD = format("\"lastUpload\":{\"dataUploadShort\":{%s}}%s", lastUploadUriPart, dataUploadsPart);

        final UploadsInfo uploadsInfo = OBJECT_MAPPER.readValue(format("{\"dataSetsInfo\":{\"sets\":[{\"meta\":{\"identifier\":%s},%s}]}}", DATASET_ID, LAST_UPLOAD), UploadsInfo.class);

        when(restTemplate.getForObject(UploadsInfo.URI_TEMPLATE.expand(PROJECT_ID), UploadsInfo.class))
                .thenReturn(uploadsInfo);
    }
}