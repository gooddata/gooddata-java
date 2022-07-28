/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.github.sardine.impl.SardineException;
import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.UriPrefixer;
import com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
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
            notNull(client, "client");
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

        /**
         * @deprecated because supertype's {@link HttpClient#getParams()} is deprecated.
         */
        @Override
        @Deprecated
        public HttpParams getParams() {
            return client.getParams();
        }

        /**
         * @deprecated because supertype's {@link HttpClient#getConnectionManager()} is deprecated.
         */
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
            notNull(wrappedResponse, "wrappedResponse");
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

        /**
         * @deprecated because supertype's {@link HttpMessage#getParams()} is deprecated.
         */
        @Deprecated
        @Override
        public HttpParams getParams() {
            return wrappedResponse.getParams();
        }

        /**
         * @deprecated because supertype's {@link HttpMessage#setParams(HttpParams)} is deprecated.
         */
        @Deprecated
        @Override
        public void setParams(HttpParams params) {
            wrappedResponse.setParams(params);
        }
    }
}
