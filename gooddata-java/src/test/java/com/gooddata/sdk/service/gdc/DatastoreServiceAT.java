/*
 * (C) 2025 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.gdc;

import com.gooddata.sdk.common.GoodDataRestException;
import com.gooddata.sdk.service.AbstractGoodDataAT;
import com.gooddata.sdk.service.GoodData;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
import static org.testng.Assert.fail;

/**
 * Data store acceptance test
 */
public class DatastoreServiceAT extends AbstractGoodDataAT {

    private String file;
    private String directory;
    private static final int ITER_MAX = 10;

    @BeforeClass
    public void setUp() throws Exception {
        directory = "/" + UUID.randomUUID().toString();
    }

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreUpload() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        file = directory + "/file.csv";
        dataStoreService.upload(file, readFromResource("/person.csv"));
    }

    @Test(groups = "datastore", dependsOnMethods = "datastoreUpload")
    public void datastoreDownload() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        final File file = File.createTempFile("file", ".txt");
        try (InputStream stream = dataStoreService.download(this.file)) {
            file.deleteOnExit();
            IOUtils.copy(stream, new FileOutputStream(file));
        } finally {
            file.delete();
        }
    }

    @Test(groups = "datastore", dependsOnMethods = "datastoreDownload")
    public void verifyRequestIdInException() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        try (InputStream ignored = dataStoreService.download(this.directory + "/none.txt")) {
            fail("The exception should contain the request_id in its stacktrace.");
        } catch (Exception e){
            assertThat(e.getCause().toString(), containsString("request_id"));
        }
    }

    @Test(groups = "datastore", dependsOnMethods = "verifyRequestIdInException")
    public void datastoreDelete() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();
        dataStoreService.delete(this.file);
        dataStoreService.delete(this.directory);

        try {
            dataStoreService.delete(this.directory);
            fail("Exception was expected, as there is nothing to delete");
        } catch (DataStoreException e) {
            assertThat(e.getCause().toString(), containsString("request_id"));
            assertThat(e.getCause().toString(), containsString("404"));
            assertThat(e.getCause(), instanceOf(GoodDataRestException.class));
        }
    }

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreConnectionsClosedAfterMultipleConnections() {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();
        directory = "/" + UUID.randomUUID().toString();
        for (int i = 0; i < ITER_MAX; i++) {
            dataStoreService.upload(directory + "/file" + i + ".csv", readFromResource("/person.csv"));
        }
        assertThat(AbstractGoodDataAT.getConnectionManager().getTotalStats().getLeased(), is(equalTo(0)));
    }

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreConnectionClosedAfterSingleConnection() throws Exception {
        DataStoreService dataStoreService = AbstractGoodDataAT.gd.getDataStoreService();

        directory = "/" + UUID.randomUUID().toString();
        file = directory + "/file.csv";
        dataStoreService.upload(file, readFromResource("/person.csv"));
        assertThat(AbstractGoodDataAT.getConnectionManager().getTotalStats().getLeased(), is(equalTo(0)));
    }

    @Test(groups = "datastore", expectedExceptions = DataStoreException.class, enabled = false,
            expectedExceptionsMessageRegExp = "(?s).* 500 .*https://github.com/.Known-limitations")
    public void shouldThrowExceptionWithMessageOnUnAuthRequest() throws Exception {
        final GoodData datastoreGd = new GoodData(AbstractGoodDataAT.getProperty("host"), AbstractGoodDataAT.getProperty("login"), AbstractGoodDataAT.getProperty("password"));
        DataStoreService dataStoreService = datastoreGd.getDataStoreService();

        try {
            final String fileWithAuth = directory + "/fileWithAuth.csv";
            dataStoreService.upload(fileWithAuth, readFromResource("/person.csv"));
        } finally {
            datastoreGd.logout();
        }
    }
}

