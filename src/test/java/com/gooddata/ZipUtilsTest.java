package com.gooddata;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ZipUtilsTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldZipSingleFile() throws Exception {
        final File file = temporaryFolder.newFile("someFile.zip");
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
        File toZipDir = temporaryFolder.newFolder("toZip");
        File toZipFile = toZipDir.toPath().resolve("a/b/someFile.zip").toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(toZipDir, output, true);
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
        File toZipDir = temporaryFolder.newFolder("toZip");
        File toZipFile = toZipDir.toPath().resolve("a/b/someFile.zip").toFile();
        toZipFile.getParentFile().mkdirs();
        toZipFile.createNewFile();
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ZipUtils.zip(toZipDir, output);
            output.close();
            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(output.toByteArray()))) {
                ZipEntry entry = zipInputStream.getNextEntry();
                assertThat(entry, notNullValue());
                assertThat(entry.getName(), is("a/b/someFile.zip"));
            }
        }
    }
}