/*
 * Copyright (C) 2004-2017, GoodData(R) Corporation. All rights reserved.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.gdc;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.GoodData;
import com.gooddata.GoodDataRestException;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import static com.gooddata.util.ResourceUtils.readFromResource;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.testng.Assert.assertEquals;
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
        DataStoreService dataStoreService = gd.getDataStoreService();

        file = directory + "/file.csv";
        dataStoreService.upload(file, readFromResource("/person.csv"));
    }

    @Test(groups = "datastore", dependsOnMethods = "datastoreUpload")
    public void datastoreDownload() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();

        final File file = File.createTempFile("file", ".txt");
        try (InputStream stream = dataStoreService.download(this.file)) {
            file.deleteOnExit();
            IOUtils.copy(stream, new FileOutputStream(file));
        } finally {
            file.delete();
        }
    }

    @Test(groups = "datastore", dependsOnMethods = "datastoreDownload")
    public void datastoreDelete() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();
        dataStoreService.delete(this.file);
        dataStoreService.delete(this.directory);

        try {
            dataStoreService.delete(this.directory);
            fail("Exception was expected, as there is nothing to delete");
        } catch (GoodDataRestException e) {
            assertEquals(404, e.getStatusCode());
        }
    }

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreConnectionsClosedAfterMultipleConnections() {
        DataStoreService dataStoreService = gd.getDataStoreService();
        directory = "/" + UUID.randomUUID().toString();
        for (int i = 0; i < ITER_MAX; i++) {
            dataStoreService.upload(directory + "/file" + i + ".csv", readFromResource("/person.csv"));
        }
        assertThat(connManager.getTotalStats().getLeased(), is(equalTo(0)));
    }

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreConnectionClosedAfterSingleConnection() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();

        directory = "/" + UUID.randomUUID().toString();
        file = directory + "/file.csv";
        dataStoreService.upload(file, readFromResource("/person.csv"));
        assertThat(connManager.getTotalStats().getLeased(), is(equalTo(0)));
    }

    @Test(groups = "datastore", expectedExceptions = DataStoreException.class, enabled = false,
            expectedExceptionsMessageRegExp = "(?s).* 500 .*https://github.com/.Known-limitations")
    public void shouldThrowExceptionWithMessageOnUnAuthRequest() throws Exception {
        final GoodData datastoreGd = new GoodData(getProperty("host"), getProperty("login"), getProperty("pass"));
        DataStoreService dataStoreService = datastoreGd.getDataStoreService();

        try {
            final String fileWithAuth = directory + "/fileWithAuth.csv";
            dataStoreService.upload(fileWithAuth, readFromResource("/person.csv"));
        } finally {
            datastoreGd.logout();
        }
    }
}
