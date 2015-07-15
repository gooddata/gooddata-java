package com.gooddata.gdc;

import com.gooddata.AbstractGoodDataAT;
import com.gooddata.gdc.DataStoreService;
import org.apache.commons.io.IOUtils;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * Data store acceptance test
 */
public class DatastoreServiceAT extends AbstractGoodDataAT {

    private String file;

    @Test(groups = "datastore", dependsOnGroups = "account")
    public void datastoreUpload() throws Exception {
        DataStoreService dataStoreService = gd.getDataStoreService();

        file = "/" + UUID.randomUUID().toString() + "/file.csv";
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
    }
}
