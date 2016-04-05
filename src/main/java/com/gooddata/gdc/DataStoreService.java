/*
 * Copyright (C) 2007-2014, GoodData(R) Corporation. All rights reserved.
 */
package com.gooddata.gdc;

import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineException;
import com.github.sardine.impl.SardineImpl;
import com.gooddata.UriPrefixer;
import org.apache.commons.lang.Validate;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Locale;

import static com.gooddata.util.Validate.notEmpty;
import static com.gooddata.util.Validate.notNull;

/**
 * Uploads, downloads, deletes, ... at datastore
 */
public class DataStoreService {

    private final Sardine sardine;
    private final GdcService gdcService;
    private final URI gdcUri;
    private final RestTemplate restTemplate;

    private UriPrefixer prefixer;


    /**
     * Creates new DataStoreService
     * @param httpClient httpClient to make datastore connection
     * @param restTemplate restTemplate to make datastore connection
     * @param gdcService used to obtain datastore URI
     * @param gdcUri complete GDC URI used to prefix possibly relative datastore path
     */
    public DataStoreService(HttpClient httpClient, RestTemplate restTemplate, GdcService gdcService, String gdcUri) {
        this.gdcService = notNull(gdcService, "gdcService");
        this.gdcUri = URI.create(notEmpty(gdcUri, "gdcUri"));
        this.restTemplate = notNull(restTemplate, "restTemplate");
        sardine = new GdcSardine(new CustomHttpClientBuilder(httpClient));
    }

    private UriPrefixer getPrefixer() {
        if (prefixer == null) {
            final String uriString = gdcService.getRootLinks().getUserStagingUri();
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
     * @throws com.gooddata.gdc.DataStoreException in case upload failed
     */
    public void upload(String path, InputStream stream) {
        notEmpty(path, "path");
        notNull(stream, "stream");
        upload(getUri(path), stream);
    }

    private void upload(URI url, InputStream stream) {
        try {
            sardine.put(url.toString(), stream);
        } catch (SardineException e) {
            if (HttpStatus.INTERNAL_SERVER_ERROR.value() == e.getStatusCode()) {
                throw new DataStoreException("Got 500 while uploading to " + url + "."
                        + "\nThis can be known limitation, see https://github.com/martiner/gooddata-java/wiki/Known-limitations", e);
            } else {
                throw new DataStoreException("Unable to upload to " + url + " got status " + e.getStatusCode(), e);
            }
        } catch (IOException e) {
            throw new DataStoreException("Unable to upload to " + url, e);
        }
    }

    /**
     * Download given path and return data as stream
     * @param path path from where to download
     * @return download stream
     * @throws com.gooddata.gdc.DataStoreException in case download failed
     */
    public InputStream download(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            return sardine.get(uri.toString());
        } catch (IOException e) {
            throw new DataStoreException("Unable to download from " + uri, e);
        }
    }

    /**
     * Delete given path from datastore.
     * @param path path to delete
     * @throws com.gooddata.gdc.DataStoreException in case delete failed
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
        } catch (RestClientException e) {
            throw new DataStoreException("Unable to delete " + uri, e);
        }
    }

    /**
     * This class is needed to provide Sardine with instance of {@link CloseableHttpClient}, because
     * used {@link com.gooddata.http.client.GoodDataHttpClient} is not Closeable at all (on purpose).
     * Thanks to that we can use proper GoodData authentication mechanism instead of basic auth.
     *
     * It creates simple closeable wrapper around plain {@link HttpClient} where {@code close()}
     * is implemented as noop (respectively for the response used).
     */
    private static class CustomHttpClientBuilder extends HttpClientBuilder {

        private final HttpClient client;

        private CustomHttpClientBuilder(HttpClient client) {
            this.client = client;
        }

        @Override
        public CloseableHttpClient build() {
            return new FakeCloseableHttpClient(client);
        }
    }

    private static class FakeCloseableHttpClient extends CloseableHttpClient {
        private final HttpClient client;

        private FakeCloseableHttpClient(HttpClient client) {
            notNull(client, "client can't be null");
            this.client = client;
        }

        @Override
        protected CloseableHttpResponse doExecute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
            // nothing to do - this method is never called, because we override all methods from CloseableHttpClient
            return null;
        }

        @Override
        public void close() throws IOException {
            // nothing to close - wrappedClient doesn't have to implement CloseableHttpClient
        }

        @Override
        @Deprecated
        public HttpParams getParams() {
            return client.getParams();
        }

        @Override
        @Deprecated
        public ClientConnectionManager getConnectionManager() {
            return client.getConnectionManager();
        }

        @Override
        public CloseableHttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
            return new FakeCloseableHttpResponse(client.execute(request));
        }

        @Override
        public CloseableHttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
            return new FakeCloseableHttpResponse(client.execute(request, context));
        }

        @Override
        public CloseableHttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
            return new FakeCloseableHttpResponse(client.execute(target, request));
        }

        @Override
        public CloseableHttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
            return new FakeCloseableHttpResponse(client.execute(target, request, context));
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
            return client.execute(request, responseHandler);
        }

        @Override
        public <T> T execute(HttpUriRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
            return client.execute(request, responseHandler, context);
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler) throws IOException, ClientProtocolException {
            return client.execute(target, request, responseHandler);
        }

        @Override
        public <T> T execute(HttpHost target, HttpRequest request, ResponseHandler<? extends T> responseHandler, HttpContext context) throws IOException, ClientProtocolException {
            return client.execute(target, request, responseHandler, context);
        }
    }

    private static class FakeCloseableHttpResponse implements CloseableHttpResponse {

        private final HttpResponse wrappedResponse;

        public FakeCloseableHttpResponse(HttpResponse wrappedResponse) {
            Validate.notNull(wrappedResponse, "wrappedResponse cannot be null!");
            this.wrappedResponse = wrappedResponse;
        }

        @Override
        public void close() throws IOException {
            // nothing to close - wrappedClient doesn't have to implement CloseableHttpResponse
        }

        @Override
        public StatusLine getStatusLine() {
            return wrappedResponse.getStatusLine();
        }

        @Override
        public void setStatusLine(StatusLine statusline) {
            wrappedResponse.setStatusLine(statusline);
        }

        @Override
        public void setStatusLine(ProtocolVersion ver, int code) {
            wrappedResponse.setStatusLine(ver, code);
        }

        @Override
        public void setStatusLine(ProtocolVersion ver, int code, String reason) {
            wrappedResponse.setStatusLine(ver, code, reason);
        }

        @Override
        public void setStatusCode(int code) throws IllegalStateException {
            wrappedResponse.setStatusCode(code);
        }

        @Override
        public void setReasonPhrase(String reason) throws IllegalStateException {
            wrappedResponse.setReasonPhrase(reason);
        }

        @Override
        public HttpEntity getEntity() {
            return wrappedResponse.getEntity();
        }

        @Override
        public void setEntity(HttpEntity entity) {
            wrappedResponse.setEntity(entity);
        }

        @Override
        public Locale getLocale() {
            return wrappedResponse.getLocale();
        }

        @Override
        public void setLocale(Locale loc) {
            wrappedResponse.setLocale(loc);
        }

        @Override
        public ProtocolVersion getProtocolVersion() {
            return wrappedResponse.getProtocolVersion();
        }

        @Override
        public boolean containsHeader(String name) {
            return wrappedResponse.containsHeader(name);
        }

        @Override
        public Header[] getHeaders(String name) {
            return wrappedResponse.getHeaders(name);
        }

        @Override
        public Header getFirstHeader(String name) {
            return wrappedResponse.getFirstHeader(name);
        }

        @Override
        public Header getLastHeader(String name) {
            return wrappedResponse.getLastHeader(name);
        }

        @Override
        public Header[] getAllHeaders() {
            return wrappedResponse.getAllHeaders();
        }

        @Override
        public void addHeader(Header header) {
            wrappedResponse.addHeader(header);
        }

        @Override
        public void addHeader(String name, String value) {
            wrappedResponse.addHeader(name, value);
        }

        @Override
        public void setHeader(Header header) {
            wrappedResponse.setHeader(header);
        }

        @Override
        public void setHeader(String name, String value) {
            wrappedResponse.setHeader(name, value);
        }

        @Override
        public void setHeaders(Header[] headers) {
            wrappedResponse.setHeaders(headers);
        }

        @Override
        public void removeHeader(Header header) {
            wrappedResponse.removeHeader(header);
        }

        @Override
        public void removeHeaders(String name) {
            wrappedResponse.removeHeaders(name);
        }

        @Override
        public HeaderIterator headerIterator() {
            return wrappedResponse.headerIterator();
        }

        @Override
        public HeaderIterator headerIterator(String name) {
            return wrappedResponse.headerIterator(name);
        }

        @Override
        public HttpParams getParams() {
            return wrappedResponse.getParams();
        }

        @Override
        public void setParams(HttpParams params) {
            wrappedResponse.setParams(params);
        }
    }
}
