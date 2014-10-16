package com.gooddata.gdc;

import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;
import static net.jadler.Jadler.verifyThatRequest;

import com.gooddata.AbstractGoodDataIT;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class DataStoreServiceIT extends AbstractGoodDataIT {


    @Test
    public void testUpload() throws Exception {
        onRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/test").respond().withStatus(201);
        mockGdc("http://localhost:" + port());
        gd.getDataStoreService().upload("/test", new ByteArrayInputStream("test".getBytes()));
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

    private void testUploadRelative(String path) throws IOException {
        onRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/uploads/test").respond().withStatus(201);
        mockGdc(path);
        gd.getDataStoreService().upload("/test", new ByteArrayInputStream("test".getBytes()));
        verifyThatRequest().havingMethodEqualTo("PUT").havingPathEqualTo("/uploads/test").receivedOnce();
    }

    private void mockGdc(String path) throws IOException {
        final String gdcBody = IOUtils.toString(getClass().getResourceAsStream("/gdc/gdc.json")).replaceAll("\\{STAGING_LINK\\}", path);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
                .respond()
                .withBody(gdcBody)
                .withStatus(200);
    }


}