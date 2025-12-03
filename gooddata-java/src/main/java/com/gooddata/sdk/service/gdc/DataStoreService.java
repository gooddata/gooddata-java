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
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.NonRepeatableRequestException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderIterator;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
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
     *
     * @param restProvider       restProvider to make datastore connection
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
     *
     * @param path path the uri is constructed for
     * @return uri for given path
     */
    public URI getUri(String path) {
        return getPrefixer().mergeUris(path);
    }

    /**
     * Uploads given stream to given datastore path
     *
     * @param path   path where to upload to
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
     *
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
     *
     * @param path path to delete
     * @throws DataStoreException in case delete failed
     */
    public void delete(String path) {
        notEmpty(path, "path");
        final URI uri = getUri(path);
        try {
            final ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, org.springframework.http.HttpEntity.EMPTY, Void.class);

            // in case we get redirect (i.e. when we want to delete collection) we will follow redirect to the new location
            if (HttpStatus.MOVED_PERMANENTLY.equals(result.getStatusCode())) {
                restTemplate.exchange(Objects.requireNonNull(result.getHeaders().getLocation()), HttpMethod.DELETE, org.springframework.http.HttpEntity.EMPTY, Void.class);
            }
        } catch (GoodDataRestException e) {
            throw new DataStoreException("Unable to delete " + uri, e);
        }
    }

    /**
     * This builder returns a {@link CloseableHttpClient} facade that adapts the internal HttpClient5
     * into the HttpClient4 API required by Sardine.
     */
    static class CustomHttpClientBuilder extends HttpClientBuilder {

        private final HttpClient client;

        private CustomHttpClientBuilder(HttpClient client) {
            this.client = notNull(client, "client");
        }

        @Override
        public CloseableHttpClient build() {
            return new ShadowHttpClient4Adapter(client);
        }
    }

    /**
     * Adapter bridging the (shaded) HttpClient4 API expected by Sardine to the HttpClient5 implementation used by the SDK.
     */
    static final class ShadowHttpClient4Adapter extends CloseableHttpClient {

        private final HttpClient client;

        private ShadowHttpClient4Adapter(HttpClient client) {
            this.client = client;
        }

        private static org.apache.hc.core5.http.protocol.HttpContext adaptContext(final HttpContext context) {
            if (context instanceof org.apache.hc.core5.http.protocol.HttpContext) {
                return (org.apache.hc.core5.http.protocol.HttpContext) context;
            }
            return null;
        }

        private static org.apache.hc.core5.http.HttpHost adaptHost(final HttpHost target, final HttpRequest request) {
            if (target != null) {
                return new org.apache.hc.core5.http.HttpHost(target.getSchemeName(), target.getHostName(), target.getPort());
            }
            if (request instanceof HttpUriRequest uriRequest) {
                final java.net.URI uri = uriRequest.getURI();
                if (uri.isAbsolute()) {
                    return new org.apache.hc.core5.http.HttpHost(uri.getScheme(), uri.getHost(), uri.getPort());
                }
            }
            return null;
        }

        private static org.apache.hc.core5.http.ClassicHttpRequest adaptRequest(
                final org.apache.hc.core5.http.HttpHost target, final HttpRequest request) throws IOException {

            final String method = request.getRequestLine().getMethod();
            final java.net.URI uri = resolveUri(target, request);
            final org.apache.hc.client5.http.classic.methods.HttpUriRequestBase builder =
                    new org.apache.hc.client5.http.classic.methods.HttpUriRequestBase(method, uri);

            for (Header header : request.getAllHeaders()) {
                builder.addHeader(header.getName(), header.getValue());
            }

            if (request instanceof org.apache.http.HttpEntityEnclosingRequest) {
                final HttpEntity entity = ((org.apache.http.HttpEntityEnclosingRequest) request).getEntity();
                if (entity != null) {
                    builder.setEntity(adaptEntity(entity));
                }
            }
            return builder;
        }

        private static java.net.URI resolveUri(final org.apache.hc.core5.http.HttpHost target, final HttpRequest request) {
            if (request instanceof HttpUriRequest) {
                final java.net.URI requestUri = ((HttpUriRequest) request).getURI();
                if (requestUri.isAbsolute() || target == null) {
                    return requestUri;
                }
                return java.net.URI.create(target.toURI()).resolve(requestUri.toString());
            }

            final String uri = request.getRequestLine().getUri();
            final java.net.URI parsed = java.net.URI.create(uri);
            if (parsed.isAbsolute() || target == null) {
                return parsed;
            }
            return java.net.URI.create(target.toURI()).resolve(uri);
        }

        private static org.apache.hc.core5.http.HttpEntity adaptEntity(final HttpEntity entity) throws IOException {
            final org.apache.hc.core5.http.ContentType contentType = entity.getContentType() != null
                    ? org.apache.hc.core5.http.ContentType.parse(entity.getContentType().getValue())
                    : null;
            return new org.apache.hc.core5.http.io.entity.InputStreamEntity(entity.getContent(), entity.getContentLength(), contentType);
        }

        @Override
        protected CloseableHttpResponse doExecute(final HttpHost target, final HttpRequest request,
                                                  final HttpContext context) throws IOException, ClientProtocolException {
            notNull(request, "request");
            final org.apache.hc.core5.http.HttpHost target5 = adaptHost(target, request);
            final org.apache.hc.core5.http.ClassicHttpRequest request5 = adaptRequest(target5, request);
            final org.apache.hc.core5.http.ClassicHttpResponse response5 = execute(target5, request5, context);
            return new ShadowedCloseableHttpResponse(response5);
        }

        private org.apache.hc.core5.http.ClassicHttpResponse execute(
                final org.apache.hc.core5.http.HttpHost target, final org.apache.hc.core5.http.ClassicHttpRequest request,
                final HttpContext context) throws IOException {
            if (target != null) {
                return client.execute(target, request, adaptContext(context), response -> response);
            }
            return client.execute(request, adaptContext(context), response -> response);
        }

        @Override
        public void close() {
            // HttpClient5 instance is managed by the SDK and must not be closed by Sardine.
        }

        @Override
        @Deprecated
        public HttpParams getParams() {
            return null;
        }

        @Override
        @Deprecated
        public ClientConnectionManager getConnectionManager() {
            return null;
        }

        static final class ShadowedCloseableHttpResponse implements CloseableHttpResponse {

            private final org.apache.hc.core5.http.ClassicHttpResponse response;

            private ShadowedCloseableHttpResponse(org.apache.hc.core5.http.ClassicHttpResponse response) {
                this.response = notNull(response, "response");
            }

            private static org.apache.hc.core5.http.Header[] convert(Header[] headers) {
                final org.apache.hc.core5.http.Header[] converted = new org.apache.hc.core5.http.Header[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    converted[i] = new org.apache.hc.core5.http.message.BasicHeader(headers[i].getName(), headers[i].getValue());
                }
                return converted;
            }

        @Override
        public void close() throws IOException {
            // nothing to close - wrappedClient doesn't have to implement CloseableHttpResponse
        }

            @Override
            public StatusLine getStatusLine() {
                final org.apache.hc.core5.http.ProtocolVersion version = response.getVersion();
                final ProtocolVersion protocolVersion = new ProtocolVersion(version.getProtocol(),
                        version.getMajor(), version.getMinor());
                return new org.apache.http.message.BasicStatusLine(protocolVersion, response.getCode(), response.getReasonPhrase());
            }

            @Override
            public void setStatusLine(StatusLine statusline) {
                response.setCode(statusline.getStatusCode());
            }

            @Override
            public void setStatusLine(ProtocolVersion ver, int code) {
                response.setVersion(new org.apache.hc.core5.http.ProtocolVersion(ver.getProtocol(), ver.getMajor(), ver.getMinor()));
                response.setCode(code);
            }

            @Override
            public void setStatusLine(ProtocolVersion ver, int code, String reason) {
                setStatusLine(ver, code);
                response.setReasonPhrase(reason);
            }

            @Override
            public void setStatusCode(int code) throws IllegalStateException {
                response.setCode(code);
            }

            @Override
            public void setReasonPhrase(String reason) throws IllegalStateException {
                response.setReasonPhrase(reason);
            }

            @Override
            public HttpEntity getEntity() {
                final org.apache.hc.core5.http.HttpEntity entity = response.getEntity();
                return entity == null ? null : new ShadowedHttpEntity(entity);
            }

            @Override
            public void setEntity(HttpEntity entity) {
                if (entity == null) {
                    response.setEntity(null);
                    return;
                }
                try {
                    response.setEntity(adaptEntity(entity));
                } catch (IOException e) {
                    throw new IllegalStateException("Unable to set entity on the Sardine shadow response", e);
                }
            }

            @Override
            public Locale getLocale() {
                return Locale.getDefault();
            }

            @Override
            public void setLocale(Locale loc) {
                // no-op
            }

            @Override
            public ProtocolVersion getProtocolVersion() {
                final org.apache.hc.core5.http.ProtocolVersion version = response.getVersion();
                return new ProtocolVersion(version.getProtocol(), version.getMajor(), version.getMinor());
            }

            @Override
            public boolean containsHeader(String name) {
                return response.containsHeader(name);
            }

            @Override
            public Header[] getHeaders(String name) {
                final org.apache.hc.core5.http.Header[] headers = response.getHeaders(name);
                final Header[] converted = new Header[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    converted[i] = new BasicHeader(headers[i].getName(), headers[i].getValue());
                }
                return converted;
            }

            @Override
            public Header getFirstHeader(String name) {
                final org.apache.hc.core5.http.Header header = response.getFirstHeader(name);
                return header == null ? null : new BasicHeader(header.getName(), header.getValue());
            }

            @Override
            public Header getLastHeader(String name) {
                final org.apache.hc.core5.http.Header header = response.getLastHeader(name);
                return header == null ? null : new BasicHeader(header.getName(), header.getValue());
            }

            @Override
            public Header[] getAllHeaders() {
                final org.apache.hc.core5.http.Header[] headers = response.getHeaders();
                final Header[] converted = new Header[headers.length];
                for (int i = 0; i < headers.length; i++) {
                    converted[i] = new BasicHeader(headers[i].getName(), headers[i].getValue());
                }
                return converted;
            }

            @Override
            public void addHeader(Header header) {
                response.addHeader(header.getName(), header.getValue());
            }

            @Override
            public void addHeader(String name, String value) {
                response.addHeader(name, value);
            }

            @Override
            public void setHeader(Header header) {
                response.setHeader(header.getName(), header.getValue());
            }

            @Override
            public void setHeader(String name, String value) {
                response.setHeader(name, value);
            }

            @Override
            public void setHeaders(Header[] headers) {
                response.setHeaders(convert(headers));
            }

            @Override
            public void removeHeader(Header header) {
                response.removeHeaders(header.getName());
            }

            @Override
            public void removeHeaders(String name) {
                response.removeHeaders(name);
            }

            @Override
            public HeaderIterator headerIterator() {
                return new BasicHeaderIterator(getAllHeaders(), null);
            }

            @Override
            public HeaderIterator headerIterator(String name) {
                return new BasicHeaderIterator(getHeaders(name), name);
            }

            @Deprecated
            @Override
            public HttpParams getParams() {
                return null;
            }

            @Deprecated
            @Override
            public void setParams(HttpParams params) {
                // no-op
            }
        }

        static final class ShadowedHttpEntity implements HttpEntity {

            private final org.apache.hc.core5.http.HttpEntity delegate;

            private ShadowedHttpEntity(org.apache.hc.core5.http.HttpEntity delegate) {
                this.delegate = delegate;
            }

            @Override
            public boolean isRepeatable() {
                return delegate.isRepeatable();
            }

            @Override
            public boolean isChunked() {
                return delegate.isChunked();
            }

            @Override
            public long getContentLength() {
                return delegate.getContentLength();
            }

            @Override
            public Header getContentType() {
                final String type = delegate.getContentType();
                return type == null ? null : new BasicHeader(org.apache.http.HttpHeaders.CONTENT_TYPE, type);
            }

            @Override
            public Header getContentEncoding() {
                final String encoding = delegate.getContentEncoding();
                return encoding == null ? null : new BasicHeader(org.apache.http.HttpHeaders.CONTENT_ENCODING, encoding);
            }

            @Override
            public InputStream getContent() throws IOException {
                return delegate.getContent();
            }

            @Override
            public void writeTo(java.io.OutputStream outstream) throws IOException {
                delegate.writeTo(outstream);
            }

            @Override
            public boolean isStreaming() {
                return delegate.isStreaming();
            }

            @Deprecated
            @Override
            public void consumeContent() throws IOException {
                org.apache.hc.core5.http.io.entity.EntityUtils.consume(delegate);
            }
        }
    }
}