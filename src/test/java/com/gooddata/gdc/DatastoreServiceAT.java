package com.gooddata.gdc;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.GoodData;
import com.gooddata.GoodDataRestException;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Data store acceptance test
 */
public class DatastoreServiceAT extends AbstractGoodDataAT {

    private String file;
    private String directory;

    @BeforeClass
    public void setUp() throws Exception {
        directory = "/" + UUID.randomUUID().toString();
    }

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreUpload() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();

        file = directory + "/file.csv";
        dataStoreService.upload(file, getClass().getResourceAsStream("/person.csv"));
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

    @Test(groups = "datastore")
    public void datastoreUploadWithAuthentication() throws Exception {

        //TODO this doesn't work because we can't read the underlying stream twice
        final GoodData datastoreGd = new GoodData(getProperty("host"), getProperty("login"), getProperty("pass"));
        DataStoreService dataStoreService = datastoreGd.getDataStoreService();

        try {
            final String fileWithAuth = directory + "/fileWithAuth.csv";
            dataStoreService.upload(fileWithAuth, getClass().getResourceAsStream("/person.csv"));
            dataStoreService.delete(fileWithAuth);
        } finally {
            datastoreGd.logout();
        }
    }
}
