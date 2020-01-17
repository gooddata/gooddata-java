/*
 * Copyright (C) 2004-2019, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.service.AbstractGoodDataIT;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static net.jadler.Jadler.onRequest;
import static net.jadler.Jadler.port;

public class DataStoreServiceIT extends AbstractGoodDataIT {

    private InputStream content;

    @BeforeMethod
    public void setUp() throws Exception {
        onRequest()
                .havingMethodEqualTo("PUT")
                .havingPathEqualTo("/uploads/test")
            .respond()
                .withStatus(201);
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
            .respond()
                .withBody(readFromResource("/gdc/gdc.json"))
                .withStatus(200);
        content = new ByteArrayInputStream("test".getBytes());
    }

    @Test
    public void shouldUploadAbsolute() throws Exception {
        gd.getDataStoreService().upload("/test", content);
    }

    @Test
    public void shouldUploadRelative() throws Exception {
        gd.getDataStoreService().upload("test", content);
    }

    @Test
    public void shouldUploadWithFullStagingLink() throws Exception {
        final String gdcBody = IOUtils.toString(readFromResource("/gdc/gdc.json"), Charset.defaultCharset())
                .replaceAll("/uploads", "http://localhost:" + port() + "/uploads");
        onRequest()
                .havingMethodEqualTo("GET")
                .havingPathEqualTo("/gdc")
            .respond()
                .withBody(gdcBody)
                .withStatus(200);

        gd.getDataStoreService().upload("/test", content);
    }

}