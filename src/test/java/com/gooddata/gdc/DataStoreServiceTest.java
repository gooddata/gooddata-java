package com.gooddata.gdc;

import org.apache.http.impl.client.HttpClientBuilder;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;

import static net.jadler.Jadler.closeJadler;
import static net.jadler.Jadler.initJadler;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static net.jadler.Jadler.verifyThatRequest;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataStoreServiceTest {

    private DataStoreService storeService;
    private GdcService gdcService;

    @BeforeMethod
    public void setUp() {
        initJadler();

        gdcService = mock(GdcService.class);
        storeService = new DataStoreService(HttpClientBuilder.create(), gdcService, "http://localhost:"+port(), "user", "pass");
    }

    @AfterMethod
    public void tearDown() {
        closeJadler();
    }

    @Test
    public void testUpload() throws Exception {
        final Gdc gdc = mock(Gdc.class);
        when(gdc.getUserStagingLink()).thenReturn("http://localhost:"+port());
        when(gdcService.getGdc()).thenReturn(gdc);
        onRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/test").respond().withStatus(201);
        storeService.upload("/test", new ByteArrayInputStream("test".getBytes()));
        verifyThatRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/test").receivedOnce();
    }

    @Test
    public void testUploadRelativePath() throws Exception {
        testUploadRelative("/uploads");
    }

    @Test
    public void testUploadRelativePathNoSlash() throws Exception {
        testUploadRelative("uploads");
    }

    private void testUploadRelative(String path) {
        final Gdc gdc = mock(Gdc.class);
        when(gdc.getUserStagingLink()).thenReturn(path);
        when(gdcService.getGdc()).thenReturn(gdc);
        onRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/uploads/test").respond().withStatus(201);
        storeService.upload("/test", new ByteArrayInputStream("test".getBytes()));
        verifyThatRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/uploads/test").receivedOnce();
    }


}