/*
 * (C) 2022 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipHelperTest {

    private static final String SOME_FILE = "someFile.txt";
    private static final String SOME_FILE_PATH = Paths.get("a", "b", SOME_FILE).toString();

    private Path temporaryFolder;

    @BeforeClass
    public void setUp() throws Exception {
        temporaryFolder = Files.createTempDirectory("zip");
    }

    @AfterClass
    public void tearDown() throws Exception {
        FileUtils.deleteQuietly(temporaryFolder.toFile());
    }

    @Test
    public void shouldZipSingleFile() throws Exception {
        final File file = temporaryFolder.resolve(SOME_FILE).toFile();
        file.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipHelper.zip(file, output);
            output.close();
            verifyZipContent(output, SOME_FILE);
        }
    }

    @Test
    public void shouldZipDir() throws Exception {
        Path toZipDir = temporaryFolder.resolve("toZip");
        toZipDir.toFile().mkdirs();
        File toZipFile = toZipDir.resolve(SOME_FILE_PATH).toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipHelper.zip(toZipDir.toFile(), output, true);
            output.close();
            verifyZipContent(output, Paths.get("toZip", "a", "b", SOME_FILE).toString());
        }
    }

    @Test
    public void shouldZipDirWithoutRoot() throws Exception {
        Path toZipDir = temporaryFolder.resolve("toZip");
        toZipDir.toFile().mkdirs();
        File toZipFile = toZipDir.resolve(SOME_FILE_PATH).toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipHelper.zip(toZipDir.toFile(), output);
            output.close();
            verifyZipContent(output, Paths.get("a", "b", SOME_FILE).toString());
        }
    }

    @Test
    public void shouldNotZipAlreadyZipped() throws Exception {
        // first prepare zipped file
        final File file = temporaryFolder.resolve(SOME_FILE).toFile();
        file.createNewFile();
        final File zipped = temporaryFolder.resolve("zipped.zip").toFile();
        try (FileOutputStream zipStream = new FileOutputStream(zipped)) {
            ZipHelper.zip(file, zipStream);
            zipStream.close();
        }

        // actual test
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipHelper.zip(zipped, output);
            output.close();
            verifyZipContent(output, SOME_FILE);
        }
    }

    private static void verifyZipContent(ByteArrayOutputStream zip, String shouldContain) throws Exception {
        try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(zip.toByteArray()))) {
            ZipEntry entry = zipInputStream.getNextEntry();
            assertThat(entry, notNullValue());
            assertThat(entry.getName(), is(shouldContain));
        }
    }
}