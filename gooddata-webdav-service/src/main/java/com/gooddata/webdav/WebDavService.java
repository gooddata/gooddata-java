/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.webdav;

/**
 * Service interface for WebDAV operations. This interface provides a clean abstraction
 * over WebDAV operations, isolating the Sardine implementation details from the main SDK.
 * 
 * This service is designed to be called over HTTP messages to maintain complete isolation
 * between the main SDK (HttpClient 5.x) and WebDAV operations (HttpClient 4.x via Sardine).
 */
public interface WebDavService {

    /**
     * Upload content to WebDAV server
     * 
     * @param request WebDAV request containing URL, headers, and content
     * @return WebDAV response with operation result
     * @throws WebDavServiceException on operation failure
     */
    WebDavResponse upload(WebDavRequest request) throws WebDavServiceException;

    /**
     * Download content from WebDAV server
     * 
     * @param request WebDAV request containing URL and headers
     * @return WebDAV response with content stream
     * @throws WebDavServiceException on operation failure
     */
    WebDavResponse download(WebDavRequest request) throws WebDavServiceException;

    /**
     * Delete resource from WebDAV server
     * 
     * @param request WebDAV request containing URL and headers
     * @return WebDAV response with operation result
     * @throws WebDavServiceException on operation failure
     */
    WebDavResponse delete(WebDavRequest request) throws WebDavServiceException;

    /**
     * Check if resource exists on WebDAV server
     * 
     * @param request WebDAV request containing URL and headers
     * @return WebDAV response with existence status
     * @throws WebDavServiceException on operation failure
     */
    WebDavResponse exists(WebDavRequest request) throws WebDavServiceException;

    /**
     * Create directory on WebDAV server
     * 
     * @param request WebDAV request containing URL and headers
     * @return WebDAV response with operation result
     * @throws WebDavServiceException on operation failure
     */
    WebDavResponse createDirectory(WebDavRequest request) throws WebDavServiceException;
}