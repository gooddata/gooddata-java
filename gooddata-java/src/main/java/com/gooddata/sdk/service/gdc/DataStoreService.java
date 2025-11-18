/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.common.UriPrefixer;
import com.gooddata.sdk.service.httpcomponents.SingleEndpointGoodDataRestProvider;
import com.gooddata.sdk.service.auth.CredentialManager;
import com.gooddata.webdav.WebDavService;
import com.gooddata.webdav.WebDavRequest;
import com.gooddata.webdav.WebDavResponse;
import com.gooddata.webdav.WebDavServiceException;
import com.gooddata.webdav.SardineWebDavService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DataStoreService.class);

    private final WebDavService webDavService;
    private final CredentialManager credentialManager;
    private final Supplier<String> stagingUriSupplier;
    private final URI gdcUri;
    private final RestTemplate restTemplate;

    private UriPrefixer prefixer;


    /**
     * Creates new DataStoreService with WebDAV service integration
     * @param restProvider restProvider to make datastore connection
     * @param stagingUriSupplier used to obtain datastore URI
     * @param webDavService WebDAV service for file operations
     * @param credentialManager credential manager for authentication sharing
     */
    public DataStoreService(SingleEndpointGoodDataRestProvider restProvider, Supplier<String> stagingUriSupplier, 
                           WebDavService webDavService, CredentialManager credentialManager) {
        notNull(restProvider, "restProvider");
        this.stagingUriSupplier = notNull(stagingUriSupplier, "stagingUriSupplier");
        this.gdcUri = URI.create(notNull(restProvider.getEndpoint(), "endpoint").toUri());
        this.restTemplate = notNull(restProvider.getRestTemplate(), "restTemplate");
        this.webDavService = notNull(webDavService, "webDavService");
        this.credentialManager = notNull(credentialManager, "credentialManager");
    }

    /**
     * Creates new DataStoreService with default WebDAV service (backward compatibility)
     * @param restProvider restProvider to make datastore connection  
     * @param stagingUriSupplier used to obtain datastore URI
     */
    public DataStoreService(SingleEndpointGoodDataRestProvider restProvider, Supplier<String> stagingUriSupplier) {
        this(restProvider, stagingUriSupplier, new SardineWebDavService(), new CredentialManager());
    }

    private UriPrefixer getPrefixer() {
        if (prefixer == null) {
            final String uriString = stagingUriSupplier.get();
            final URI uri = URI.create(uriString);
            prefixer = new UriPrefixer(uri.isAbsolute() ? uri : gdcUri.resolve(uriString));
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
            // Ensure WebDAV service has current credentials before upload
            configureWebDavCredentials();
            
            WebDavRequest request = WebDavRequest.builder()
                    .url(url.toString())
                    .method("PUT")
                    .content(stream)
                    .headers(credentialManager.getAuthHeaders())
                    .build();
                    
            WebDavResponse response = webDavService.upload(request);
            
            if (!response.isSuccess()) {
                int statusCode = response.getStatusCode();
                if (HttpStatus.INTERNAL_SERVER_ERROR.value() == statusCode) {
                    // this error may occur when user issues request to WebDAV before SST and TT were obtained
                    // and WebDAV is deployed on a separate hostname
                    // see https://github.com/gooddata/gooddata-java/wiki/Known-limitations
                    throw new DataStoreException(createUnAuthRequestWarningMessage(url), new Exception("WebDAV response status: " + statusCode));
                } else {
                    throw new DataStoreException("Unable to upload to " + url + " got status " + statusCode + ": " + response.getErrorMessage(), new Exception("WebDAV upload failed"));
                }
            }
        } catch (WebDavServiceException e) {
            // Check for authentication-related errors
            if (e.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value() || 
                e.getMessage().contains("Connection") || e.getMessage().contains("authentication")) {
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
            WebDavRequest request = WebDavRequest.builder()
                    .url(uri.toString())
                    .method("GET")
                    .headers(credentialManager.getAuthHeaders())
                    .build();
                    
            WebDavResponse response = webDavService.download(request);
            
            if (!response.isSuccess()) {
                throw new DataStoreException("Unable to download from " + uri + " got status " + response.getStatusCode() + ": " + response.getErrorMessage(), new Exception("WebDAV download failed"));
            }
            
            return response.getContent();
        } catch (WebDavServiceException e) {
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
            final ResponseEntity<Void> result = restTemplate.exchange(uri, HttpMethod.DELETE, org.springframework.http.HttpEntity.EMPTY, Void.class);

            // in case we get redirect (i.e. when we want to delete collection) we will follow redirect to the new location
            if (HttpStatus.MOVED_PERMANENTLY.equals(result.getStatusCode())) {
                restTemplate.exchange(result.getHeaders().getLocation(), HttpMethod.DELETE, org.springframework.http.HttpEntity.EMPTY, Void.class);
            }
        } catch (GoodDataRestException e) {
            throw new DataStoreException("Unable to delete " + uri, e);
        }
    }

    /**
     * Configure WebDAV service with current credentials from credential manager.
     * This ensures that the WebDAV service has the most up-to-date authentication
     * information before performing operations.
     */
    private void configureWebDavCredentials() {
        logger.debug("Configuring WebDAV credentials - webDavService type: {}", webDavService.getClass().getSimpleName());
        logger.debug("Credential manager has authentication: {}", credentialManager.hasAuthentication());
        
        if (webDavService instanceof SardineWebDavService && credentialManager.hasAuthentication()) {
            String username = credentialManager.getUsername();
            String password = credentialManager.getPassword();
            logger.debug("Retrieved credentials - username: {}, password: {}", 
                        username != null ? username : "null", 
                        password != null ? "[HIDDEN]" : "null");
            
            if (username != null && password != null) {
                ((SardineWebDavService) webDavService).setCredentials(username, password);
                logger.debug("Successfully set WebDAV credentials for user: {}", username);
            } else {
                logger.warn("Cannot set WebDAV credentials - username or password is null");
            }
        } else {
            logger.warn("WebDAV credential configuration skipped - webDavService instanceof SardineWebDavService: {}, hasAuthentication: {}", 
                       webDavService instanceof SardineWebDavService, 
                       credentialManager.hasAuthentication());
        }
    }

}
