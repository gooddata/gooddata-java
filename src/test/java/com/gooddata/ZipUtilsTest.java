package com.gooddata;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
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
        final File file = temporaryFolder.resolve("someFile.zip").toFile();
        file.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(file, output);
            output.close();
            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(output.toByteArray()))) {
                ZipEntry entry = zipInputStream.getNextEntry();
                assertThat(entry, notNullValue());
                assertThat(entry.getName(), is("someFile.zip"));
            }
        }
    }

    @Test
    public void shouldZipDir() throws Exception {
        Path toZipDir = temporaryFolder.resolve("toZip");
        toZipDir.toFile().mkdirs();
        File toZipFile = toZipDir.resolve("a/b/someFile.zip").toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(toZipDir.toFile(), output, true);
            output.close();
            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(output.toByteArray()))) {
                ZipEntry entry = zipInputStream.getNextEntry();
                assertThat(entry, notNullValue());
                assertThat(entry.getName(), is("toZip/a/b/someFile.zip"));
            }
        }
    }

    @Test
    public void shouldZipDirWithoutRoot() throws Exception {
        Path toZipDir = temporaryFolder.resolve("toZip");
        toZipDir.toFile().mkdirs();
        File toZipFile = toZipDir.resolve("a/b/someFile.zip").toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(toZipDir.toFile(), output);
            output.close();
            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(output.toByteArray()))) {
                ZipEntry entry = zipInputStream.getNextEntry();
                assertThat(entry, notNullValue());
                assertThat(entry.getName(), is("a/b/someFile.zip"));
            }
        }
    }
}