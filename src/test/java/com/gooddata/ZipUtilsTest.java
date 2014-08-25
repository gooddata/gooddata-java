package com.gooddata;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
                assertNotNull(entry);
                assertEquals("someFile.zip", entry.getName());
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
            ZipUtils.zip(toZipDir, output);
            output.close();
            try (ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(output.toByteArray()))) {
                ZipEntry entry = zipInputStream.getNextEntry();
                assertNotNull(entry);
                assertEquals("toZip/a/b/someFile.zip", entry.getName());
            }
        }
    }
}