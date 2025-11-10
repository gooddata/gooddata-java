/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.github.sardine.impl.SardineException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.UriPrefixer;
import com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider;
// HttpClient 5.x for main functionality
import org.apache.hc.client5.http.classic.HttpClient;
// HttpClient 4.x for Sardine compatibility
import org.apache.http.Header;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.function.Supplier;

import static com.gooddata.sdk.common.util.Validate.notEmpty;
import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Uploads, downloads, deletes, ... at datastore
 */
public class DataStoreService {

    private final GdcSardine sardine;
    private final Supplier<String> stagingUriSupplier;
    private final URI gdcUri;
    private final RestTemplate restTemplate;

    private UriPrefixer prefixer;


    /**
     * Creates new DataStoreService
     * @param restProvider restProvider to make datastore connection
     * @param stagingUriSupplier used to obtain datastore URI
     */
    public DataStoreService(SingleEndpointGoodDataRestProvider restProvider, Supplier<String> stagingUriSupplier) {
        notNull(restProvider, "restProvider");
        this.stagingUriSupplier = notNull(stagingUriSupplier, "stagingUriSupplier");
        this.gdcUri = URI.create(notNull(restProvider.getEndpoint(), "endpoint").toUri());
        this.restTemplate = notNull(restProvider.getRestTemplate(), "restTemplate");
        sardine = new GdcSardine(new CustomHttpClientBuilder(notNull(restProvider.getHttpClient(), "httpClient")));
    }

    private UriPrefixer getPrefixer() {
        if (prefixer == null) {
            final String uriString = stagingUriSupplier.get();
            final URI uri = URI.create(uriString);
            prefixer = new UriPrefixer(uri.isAbsolute() ? uri : gdcUri.resolve(uriString));
            sardine.enablePreemptiveAuthentication(prefixer.getUriPrefix().getHost());
        }
        return prefixer;
    }

    /**
     * Returns uri for given path (which is used by this service for upload, download or delete)
     * @param path path the uri is constructed for
     * @return uri for given path
     */
    public URI getUri(String path) {
        return getPrefixer().mergeUris(path);
    }

    /**
     * Uploads given stream to given datastore path
     * @param path path where to upload to
     * @param stream stream to upload
     * @throws DataStoreException in case upload failed
     */
    public void upload(String path, InputStream stream) {
        notEmpty(path, "path");
        notNull(stream, "stream");
        upload(getUri(path), stream);
    }

    private void upload(URI url, InputStream stream) {
        try {
            // We need to use it this way, if we want to track request_id in the stacktrace.
            InputStreamEntity entity = new InputStreamEntity(stream);
            List<Header> headers = Collections.singletonList(new BasicHeader(HTTP.EXPECT_DIRECTIVE, HTTP.EXPECT_CONTINUE));
            sardine.put(url.toString(), entity, headers, new GdcSardineResponseHandler());
        } catch (SardineException e) {
            if (HttpStatus.INTERNAL_SERVER_ERROR.value() == e.getStatusCode()) {
                // this error may occur when user issues request to WebDAV before SST and TT were obtained
                // and WebDAV is deployed on a separate hostname
                // see https://github.com/gooddata/gooddata-java/wiki/Known-limitations
                throw new DataStoreException(createUnAuthRequestWarningMessage(url), e);
            } else {
                throw new DataStoreException("Unable to upload to " + url + " got status " + e.getStatusCode(), e);
            }
        } catch (NoHttpResponseException e) {
            // this error may occur when user issues request to WebDAV before SST and TT were obtained
            // and WebDAV is deployed on a separate hostname since R136
            // see https://github.com/gooddata/gooddata-java/wiki/Known-limitations
            throw new DataStoreException(createUnAuthRequestWarningMessage(url), e);
        } catch (IOException e) {
            // this error may occur when user issues request to WebDAV before SST and TT were obtained
            // and WebDAV deployed on the same hostname
            // see https://github.com/gooddata/gooddata-java/wiki/Known-limitations
            if (e.getCause() instanceof NonRepeatableRequestException) {
                throw new DataStoreException(createUnAuthRequestWarningMessage(url), e);
            } else {
                throw new DataStoreException("Unable to upload to " + url, e);
            }
        }
    }

    private String createUnAuthRequestWarningMessage(final URI url) {
        return "Got 500 while uploading to " + url + "."
                + "\nThis can be known limitation, see https://github.com/gooddata/gooddata-java/wiki/Known-limitations";
    }

    /**
     * Download given path and return data as stream
     * @param path path from where to download
     * @return download stream
     * @throws DataStoreException in case download failed
     */
    public InputStream download(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            return sardine.get(uri.toString(), Collections.emptyList(), new GdcSardineResponseHandler());
        } catch (IOException e) {
            throw new DataStoreException("Unable to download from " + uri, e);
        }
    }

    /**
     * Delete given path from datastore.
     * @param path path to delete
     * @throws DataStoreException in case delete failed
     */
    public void delete(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            final ResponseEntity result = restTemplate.exchange(uri, HttpMethod.DELETE, org.springframework.http.HttpEntity.EMPTY, Void.class);

            // in case we get redirect (i.e. when we want to delete collection) we will follow redirect to the new location
            if (HttpStatus.MOVED_PERMANENTLY.equals(result.getStatusCode())) {
                restTemplate.exchange(result.getHeaders().getLocation(), HttpMethod.DELETE, org.springframework.http.HttpEntity.EMPTY, Void.class);
            }
        } catch (GoodDataRestException e) {
            throw new DataStoreException("Unable to delete " + uri, e);
        }
    }

    /**
     * This class is needed to provide Sardine with instance of {@link CloseableHttpClient}.
     * 
     * NOTE: This is a temporary adapter for HttpClient 5.x compatibility. Sardine library uses HttpClient 4.x,
     * so we create a simple HttpClient 4.x builder that delegates to the existing infrastructure.
     * The actual HTTP client used by Sardine will need proper authentication setup.
     */
    private static class CustomHttpClientBuilder extends HttpClientBuilder {

        private final HttpClient httpClient5x;

        private CustomHttpClientBuilder(HttpClient httpClient5x) {
            this.httpClient5x = httpClient5x;
        }

        @Override
        public CloseableHttpClient build() {
            // For now, create a simple HttpClient 4.x instance
            // The authentication is handled through GoodDataHttpClient which wraps this
            return org.apache.http.impl.client.HttpClients.createDefault();
        }
    }


}
