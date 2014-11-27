package com.gooddata.util;

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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ZipUtilsTest {

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
        final File file = temporaryFolder.resolve("someFile.txt").toFile();
        file.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(file, output);
            output.close();
            verifyZipContent(output, "someFile.txt");
        }
    }

    @Test
    public void shouldZipDir() throws Exception {
        Path toZipDir = temporaryFolder.resolve("toZip");
        toZipDir.toFile().mkdirs();
        File toZipFile = toZipDir.resolve("a/b/someFile.txt").toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(toZipDir.toFile(), output, true);
            output.close();
            verifyZipContent(output, "toZip/a/b/someFile.txt");
        }
    }

    @Test
    public void shouldZipDirWithoutRoot() throws Exception {
        Path toZipDir = temporaryFolder.resolve("toZip");
        toZipDir.toFile().mkdirs();
        File toZipFile = toZipDir.resolve("a/b/someFile.txt").toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(toZipDir.toFile(), output);
            output.close();
            verifyZipContent(output, "a/b/someFile.txt");
        }
    }

    @Test
    public void shouldNotZipAlreadyZipped() throws Exception {
        // first prepare zipped file
        final File file = temporaryFolder.resolve("someFile.txt").toFile();
        file.createNewFile();
        final File zipped = temporaryFolder.resolve("zipped.zip").toFile();
        try (FileOutputStream zipStream = new FileOutputStream(zipped)) {
            ZipUtils.zip(file, zipStream);
            zipStream.close();
        }

        // actual test
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(zipped, output);
            output.close();
            verifyZipContent(output, "someFile.txt");
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