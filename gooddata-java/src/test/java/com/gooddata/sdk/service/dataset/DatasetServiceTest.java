/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.dataset;


import com.gooddata.sdk.common.GoodDataException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.model.dataset.*;
import com.gooddata.sdk.model.gdc.AboutLinks.Link;
import com.gooddata.sdk.model.project.Project;
import com.gooddata.sdk.service.GoodDataSettings;
import com.gooddata.sdk.service.gdc.DataStoreException;
import com.gooddata.sdk.service.gdc.DataStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Map;

import static com.gooddata.sdk.common.util.ResourceUtils.OBJECT_MAPPER;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;
import org.springframework.http.client.reactive.ClientHttpRequest;
import reactor.util.context.Context;

import java.nio.charset.Charset;
import java.util.function.Consumer;
import java.util.function.Function;





@ExtendWith(MockitoExtension.class)
public class DatasetServiceTest {

    private static final String PROJECT_ID = "17";
    private static final String DATASET_ID = "11";

    @Mock
    private WebClient webClient;
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

    
    @BeforeEach
    void setUp() {
        service = new DatasetService(webClient, dataStoreService, new GoodDataSettings());
        lenient().when(project.getId()).thenReturn(PROJECT_ID);
    }

    @Test
    void testGetDatasetManifestWithNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.getDatasetManifest(null, DATASET_ID));
    }

    @Test
    void testGetDatasetManifestWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.getDatasetManifest(project, null));
    }


    @Test
    void testGetDatasetManifestWithEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> service.getDatasetManifest(project, ""));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetDatasetManifestWhenNotFound() {
        TestHeadersSpec headersSpec = mock(TestHeadersSpec.class);
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);


        when(uriSpec.uri(anyString())).thenReturn(headersSpec);

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DatasetManifest.class))
                .thenReturn(Mono.error(new GoodDataRestException(404, "", "", "", "")));

        assertThrows(DatasetNotFoundException.class, () -> service.getDatasetManifest(project, DATASET_ID));
    }




        // Minimal stub that satisfies S extends RequestHeadersSpec<S>
        static class TestHeadersSpec implements WebClient.RequestHeadersSpec<TestHeadersSpec> {
            @Override public WebClient.ResponseSpec retrieve() { return null; }
            @Override public TestHeadersSpec accept(MediaType... mediaTypes) { return this; }
            @Override public TestHeadersSpec acceptCharset(Charset... charsets) { return this; }
            @Override public TestHeadersSpec cookie(String name, String value) { return this; }
            @Override public TestHeadersSpec cookies(Consumer<MultiValueMap<String, String>> cookiesConsumer) { return this; }
            @Override public TestHeadersSpec header(String headerName, String... headerValues) { return this; }
            @Override public TestHeadersSpec headers(Consumer<HttpHeaders> headersConsumer) { return this; }
            @Override public TestHeadersSpec ifModifiedSince(ZonedDateTime ifModifiedSince) { return this; }
            @Override public TestHeadersSpec ifNoneMatch(String... ifNoneMatch) { return this; }
            @Override public <V> Mono<V> exchangeToMono(Function<ClientResponse, ? extends Mono<V>> responseHandler) { throw new UnsupportedOperationException(); }
            @Override public TestHeadersSpec httpRequest(Consumer<ClientHttpRequest> requestConsumer) { return this; }
            @Override public TestHeadersSpec attributes(Consumer<Map<String, Object>> attributesConsumer) { return this; }
            @Override public TestHeadersSpec attribute(String name, Object value) { return this; }
            @Override public Mono<ClientResponse> exchange() { throw new UnsupportedOperationException(); }
            @Override public <V> Flux<V> exchangeToFlux(Function<ClientResponse, ? extends Flux<V>> responseHandler) { throw new UnsupportedOperationException(); }
            @Override public TestHeadersSpec context(Function<Context, Context> contextModifier) { return this; }
        }


    @SuppressWarnings("rawtypes")
    @Test
    void testGetDatasetManifestWhenRestClientError() {
        // Use raw types to avoid generic incompatibility
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); // raw type!
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   // raw type!
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(DatasetManifest.URI), eq(PROJECT_ID), eq(DATASET_ID))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(DatasetManifest.class))
                .thenReturn(Mono.error(new RuntimeException("RestClientException")));

        assertThrows(DatasetException.class,
                () -> service.getDatasetManifest(project, DATASET_ID));
    }



    @Test
    void testLoadDatasetWithNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(null, manifest, stream));
    }

    @Test
    void testLoadDatasetWithNullManifest() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(project, (DatasetManifest) null, stream));
    }

    @Test
    void testLoadDatasetWithNullDataset() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(project, manifest, null));
    }

    @Test
    void testLoadDatasetWhenUploadFails() {
        doThrow(DataStoreException.class).when(dataStoreService).upload(anyString(), eq(stream));
        when(manifest.getFile()).thenReturn("");
        when(manifest.getSource()).thenReturn(stream);
        when(manifest.getDataSet()).thenReturn(DATASET_ID);
        assertThrows(DatasetException.class, () -> service.loadDatasets(project, manifest));
    }

    @Test
    void testLoadDatasetByIdWithNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(null, DATASET_ID, stream));
    }

    @Test
    void testLoadDatasetByIdWithNullId() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(project, (String) null, stream));
    }

    @Test
    void testLoadDatasetByIdWithEmptyId() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(project, "", stream));
    }

    @Test
    void testLoadDatasetByIdWithNullInputStream() {
        assertThrows(IllegalArgumentException.class, () -> service.loadDataset(project, DATASET_ID, null));
    }

    @Test
    void testListDatasetLinksWithNullProject() {
        assertThrows(IllegalArgumentException.class, () -> service.listDatasetLinks(null));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testListDatasetLinksWithNullResponse() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(DatasetLinks.URI), eq(PROJECT_ID))).thenReturn(headersSpec); 
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Link.class)).thenReturn(Mono.justOrEmpty(null));

        assertThrows(GoodDataException.class, () -> service.listDatasetLinks(project));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testListDatasetLinksWithRestClientError() {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(eq(DatasetLinks.URI), eq(PROJECT_ID))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Link.class)).thenReturn(Mono.error(new RuntimeException("RestClientException")));

        assertThrows(GoodDataException.class, () -> service.listDatasetLinks(project));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testGetDataSetInfoRestClientError() {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(eq(UploadsInfo.URI), eq(PROJECT_ID))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UploadsInfo.class)).thenReturn(Mono.error(new RuntimeException("RestClientException")));

        assertThrows(GoodDataException.class, () -> service.getDataSetInfo(project, "dataset.id"));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testGetDataSetInfoEmptyResponse() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(eq(UploadsInfo.URI), eq(PROJECT_ID))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(responseSpec.bodyToMono(UploadsInfo.class)).thenReturn(Mono.empty());

        assertThrows(GoodDataException.class, () -> service.getDataSetInfo(project, "dataset.id"));
    }


    @Test
    void testListUploadsForDatasetMissingUri() throws Exception {
        mockDataSetInfo(null, null);

        assertThat(service.listUploadsForDataset(project, DATASET_ID), empty());
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testListUploadsForDatasetRestClientError() throws Exception {
        mockDataSetInfo(null, null);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); // raw type!
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   // raw type!
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(DATASET_UPLOADS_URI))).thenReturn(headersSpec); // raw type lets this work!
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Uploads.class)).thenReturn(Mono.error(new RuntimeException("error")));

        assertThrows(GoodDataException.class, () -> service.listUploadsForDataset(project, DATASET_ID));
    }


    @SuppressWarnings("rawtypes")
    @Test
    void testListUploadsForDatasetEmptyResponse() throws Exception {

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(DATASET_UPLOADS_URI))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Uploads.class)).thenReturn(Mono.empty());

        assertThrows(GoodDataException.class, () -> service.listUploadsForDataset(project, DATASET_ID));
    }



    @Test
    public void testGetLastUploadForDatasetMissingUri() throws Exception {
        mockDataSetInfo(DATASET_UPLOADS_URI, null);

        assertThat(service.getLastUploadForDataset(project, DATASET_ID), nullValue());
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetLastUploadForDatasetRestClientError() throws Exception {
        final String lastUploadUri = "last/upload/uri";
        mockDataSetInfo(null, lastUploadUri);

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); // raw type!
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   // raw type!
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(lastUploadUri))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(Upload.class)).thenReturn(Mono.error(new RuntimeException("error")));

        assertThrows(GoodDataException.class, () -> service.getLastUploadForDataset(project, DATASET_ID));
    }

    @SuppressWarnings("rawtypes")
    @Test
    void testGetUploadStatisticsWhenRestClientError() {
        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); // raw type!
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   // raw type!
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);

        when(uriSpec.uri(anyString(), (Object[]) any())).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UploadStatistics.class)).thenReturn(Mono.error(new RuntimeException("error")));

        assertThrows(GoodDataException.class, () -> service.getUploadStatistics(project));
    }


    @SuppressWarnings("rawtypes")
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

        final UploadsInfo uploadsInfo = OBJECT_MAPPER.readValue(
            format("{\"dataSetsInfo\":{\"sets\":[{\"meta\":{\"identifier\":%s},%s}]}}", DATASET_ID, LAST_UPLOAD),
            UploadsInfo.class
        );

        WebClient.RequestHeadersUriSpec uriSpec = mock(WebClient.RequestHeadersUriSpec.class); // raw type!
        WebClient.RequestHeadersSpec headersSpec = mock(WebClient.RequestHeadersSpec.class);   // raw type!
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn(uriSpec);
        when(uriSpec.uri(eq(DatasetService.UPLOADS_INFO_TEMPLATE.expand(PROJECT_ID)))).thenReturn(headersSpec);
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UploadsInfo.class)).thenReturn(Mono.just(uploadsInfo));
    }

}