/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.webdav;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.github.sardine.Sardine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public class SardineWebDavServiceTest {

    @Mock
    private Sardine mockSardine;

    private SardineWebDavService webDavService;

    @BeforeMethod
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        webDavService = new SardineWebDavService(mockSardine);
    }

    @Test
    public void testUploadSuccess() throws Exception {
        // Given
        String url = "https://example.com/webdav/test.txt";
        InputStream content = new ByteArrayInputStream("test content".getBytes());
        
        WebDavRequest request = WebDavRequest.builder()
                .url(url)
                .method("PUT")
                .content(content)
                .contentLength(12)
                .headers(Map.of("Content-Type", "text/plain"))
                .build();

        // When
        WebDavResponse response = webDavService.upload(request);

        // Then
        verify(mockSardine).put(eq(url), eq(content), eq("12"));
        assertTrue(response.isSuccess());
        assertEquals(response.getStatusCode(), 201);
    }

    @Test
    public void testDownloadSuccess() throws Exception {
        // Given
        String url = "https://example.com/webdav/test.txt";
        InputStream expectedContent = new ByteArrayInputStream("downloaded content".getBytes());
        
        when(mockSardine.get(url)).thenReturn(expectedContent);
        
        WebDavRequest request = WebDavRequest.builder()
                .url(url)
                .method("GET")
                .build();

        // When
        WebDavResponse response = webDavService.download(request);

        // Then
        verify(mockSardine).get(url);
        assertTrue(response.isSuccess());
        assertEquals(response.getStatusCode(), 200);
        assertNotNull(response.getContent());
    }

    @Test
    public void testDeleteSuccess() throws Exception {
        // Given
        String url = "https://example.com/webdav/test.txt";
        
        WebDavRequest request = WebDavRequest.builder()
                .url(url)
                .method("DELETE")
                .build();

        // When
        WebDavResponse response = webDavService.delete(request);

        // Then
        verify(mockSardine).delete(url);
        assertTrue(response.isSuccess());
        assertEquals(response.getStatusCode(), 204);
    }

    @Test
    public void testExistsTrue() throws Exception {
        // Given
        String url = "https://example.com/webdav/test.txt";
        when(mockSardine.exists(url)).thenReturn(true);
        
        WebDavRequest request = WebDavRequest.builder()
                .url(url)
                .method("HEAD")
                .build();

        // When
        WebDavResponse response = webDavService.exists(request);

        // Then
        verify(mockSardine).exists(url);
        assertTrue(response.isSuccess());
        assertEquals(response.getStatusCode(), 200);
    }

    @Test
    public void testExistsFalse() throws Exception {
        // Given
        String url = "https://example.com/webdav/test.txt";
        when(mockSardine.exists(url)).thenReturn(false);
        
        WebDavRequest request = WebDavRequest.builder()
                .url(url)
                .method("HEAD")
                .build();

        // When
        WebDavResponse response = webDavService.exists(request);

        // Then
        verify(mockSardine).exists(url);
        assertTrue(response.isSuccess());
        assertEquals(response.getStatusCode(), 404);
    }

    @Test(expectedExceptions = WebDavServiceException.class)
    public void testUploadWithIOException() throws Exception {
        // Given
        String url = "https://example.com/webdav/test.txt";
        InputStream content = new ByteArrayInputStream("test content".getBytes());
        
        WebDavRequest request = WebDavRequest.builder()
                .url(url)
                .method("PUT")
                .content(content)
                .build();

        doThrow(new IOException("Connection failed")).when(mockSardine).put(eq(url), eq(content), (String) isNull());

        // When
        webDavService.upload(request);

        // Then exception should be thrown
    }

    @Test(expectedExceptions = WebDavServiceException.class, 
          expectedExceptionsMessageRegExp = "WebDAV request cannot be null")
    public void testUploadWithNullRequest() throws Exception {
        webDavService.upload(null);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, 
          expectedExceptionsMessageRegExp = "URL is required")
    public void testUploadWithEmptyUrl() throws Exception {
        WebDavRequest.builder()
                .url("")
                .method("PUT")
                .build();
    }
}