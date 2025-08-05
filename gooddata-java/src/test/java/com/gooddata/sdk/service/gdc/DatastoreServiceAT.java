/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.service.GoodData;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*; 
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import static com.gooddata.sdk.common.util.ResourceUtils.readFromResource;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class DatastoreServiceAT extends AbstractGoodDataAT {

    private String file;
    private String directory;
    private static final int ITER_MAX = 10;

    @BeforeAll 
    static void globalSetUp() {
       
    }

    @BeforeEach 
    void setUp() {
        directory = "/" + UUID.randomUUID().toString();
    }

    @Test
    @Order(1)
    void datastoreUpload() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        file = directory + "/file.csv";
        dataStoreService.upload(file, readFromResource("/person.csv"));
    }

    @Test
    @Order(2)
    void datastoreDownload() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        final File file = File.createTempFile("file", ".txt");
        try (InputStream stream = dataStoreService.download(this.file)) {
            file.deleteOnExit();
            IOUtils.copy(stream, new FileOutputStream(file));
        } finally {
            file.delete();
        }
    }

    @Test
    @Order(3)
    void verifyRequestIdInException() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        Exception thrown = Assertions.assertThrows(Exception.class, () -> {
            try (InputStream ignored = dataStoreService.download(this.directory + "/none.txt")) {

            }
        });
        assertThat(thrown.getCause().toString(), containsString("request_id"));
    }

    @Test
    @Order(4)
    void datastoreDelete() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();
        dataStoreService.delete(this.file);
        dataStoreService.delete(this.directory);

        DataStoreException thrown = Assertions.assertThrows(DataStoreException.class, () -> {
            dataStoreService.delete(this.directory);
        });
        assertThat(thrown.getCause().toString(), containsString("request_id"));
        assertThat(thrown.getCause().toString(), containsString("404"));
        assertThat(thrown.getCause(), instanceOf(GoodDataRestException.class));
    }

    @Test
    @Order(5)
    void datastoreConnectionsClosedAfterMultipleConnections() {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();
        directory = "/" + UUID.randomUUID().toString();
        for (int i = 0; i < ITER_MAX; i++) {
            dataStoreService.upload(directory + "/file" + i + ".csv", readFromResource("/person.csv"));
        }
        //assertThat(AbstractGoodDataAT.connManager.getTotalStats().getLeased(), is(equalTo(0)));
        //removed for webclient
    }

    @Test
    @Order(6)
    void datastoreConnectionClosedAfterSingleConnection() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        directory = "/" + UUID.randomUUID().toString();
        file = directory + "/file.csv";
        dataStoreService.upload(file, readFromResource("/person.csv"));
       // assertThat(AbstractGoodDataAT.connManager.getTotalStats().getLeased(), is(equalTo(0)));
       //removed for webclient
    }

    @Test
    @Order(7)
    @Disabled("Known-limitations: 500 error expected; enable if needed") 
    void shouldThrowExceptionWithMessageOnUnAuthRequest() throws Exception {
        final GoodData datastoreGd = new GoodData(
                AbstractGoodDataAT.getProperty("host"),
                AbstractGoodDataAT.getProperty("login"),
                AbstractGoodDataAT.getProperty("password"));
        DataStoreService dataStoreService = datastoreGd.getDataStoreService();

        try {
            final String fileWithAuth = directory + "/fileWithAuth.csv";
            Assertions.assertThrows(DataStoreException.class, () -> {
                dataStoreService.upload(fileWithAuth, readFromResource("/person.csv"));
            }, ".* 500 .*https://github.com/.Known-limitations");
        } finally {
            datastoreGd.logout();
        }
    }
}
