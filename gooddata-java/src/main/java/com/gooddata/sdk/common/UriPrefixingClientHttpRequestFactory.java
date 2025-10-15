/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.common;

import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

import static com.gooddata.sdk.common.util.Validate.notNull;

/**
 * Spring 6 compatible {@link ClientHttpRequestFactory} that prefixes URIs with a base URI.
 * This implementation bridges HttpClient 4.x with Spring 6 by wrapping any ClientHttpRequestFactory
 * and automatically prepending a base URI to all requests.
 * 
 * This replaces the removed AsyncClientHttpRequestFactory functionality while maintaining
 * compatibility with HttpClient 4.x through HttpComponentsClientHttpRequestFactory.
 */
public class UriPrefixingClientHttpRequestFactory implements ClientHttpRequestFactory {

    private final ClientHttpRequestFactory requestFactory;
    private final URI baseUri;

    /**
     * Create a new UriPrefixingClientHttpRequestFactory.
     * 
     * @param requestFactory the underlying request factory (typically HttpComponentsClientHttpRequestFactory for HttpClient 4.x)
     * @param baseUri the base URI to prepend to all requests
     */
    public UriPrefixingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory, URI baseUri) {
        this.requestFactory = notNull(requestFactory, "requestFactory");
        this.baseUri = notNull(baseUri, "baseUri");
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return requestFactory.createRequest(createUri(uri), httpMethod);
    }

    /**
     * Create the full URI by combining the base URI with the provided URI.
     * This method ensures the result always has complete host information.
     * 
     * @param uri the request URI (can be relative or absolute)
     * @return the combined URI with base URI prepended and complete host info
     */
    private URI createUri(URI uri) {
        Assert.notNull(uri, "URI must not be null");
        
        // Always ensure we return an absolute URI with complete host information
        // This is critical for GoodDataHttpClient which extracts host from the URI
        
        if (uri.isAbsolute() && uri.getHost() != null && !uri.getHost().equals(baseUri.getHost())) {
            // URI has different host - return as-is
            return uri;
        }
        
        // Build complete URI with host information from baseUri
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance()
                .scheme(baseUri.getScheme())
                .host(baseUri.getHost())
                .port(baseUri.getPort());
        
        // Handle path - combine base path with request path
        String basePath = baseUri.getPath();
        String requestPath = uri.getPath();
        
        if (requestPath != null) {
            if (requestPath.startsWith("/")) {
                // Absolute path - use as-is
                builder.path(requestPath);
            } else {
                // Relative path - append to base path
                String combinedPath = (basePath != null && !basePath.endsWith("/")) ? basePath + "/" + requestPath : requestPath;
                builder.path(combinedPath);
            }
        } else {
            builder.path(basePath);
        }
        
        // Add query and fragment if present
        if (uri.getQuery() != null) {
            builder.query(uri.getQuery());
        }
        
        if (uri.getFragment() != null) {
            builder.fragment(uri.getFragment());
        }
        
        URI result = builder.build().toUri();
        
        // Ensure the result has host information - this is critical!
        if (result.getHost() == null) {
            throw new IllegalStateException("Generated URI missing host information: " + result + 
                " (baseUri: " + baseUri + ", requestUri: " + uri + ")");
        }
        
        return result;
    }

    /**
     * Get the underlying request factory.
     * 
     * @return the wrapped ClientHttpRequestFactory
     */
    public ClientHttpRequestFactory getRequestFactory() {
        return requestFactory;
    }

    /**
     * Get the base URI.
     * 
     * @return the base URI used for prefixing
     */
    public URI getBaseUri() {
        return baseUri;
    }
}
