package com.gooddata;

import static com.gooddata.Validate.notNull;

import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for manipulating zip archives.
 */
public abstract class ZipUtils {

    /**
     * This method compresses the input file to zip format. If the given file is a directory, it recursively
     * packs the directory into the output.
     *
     * @param file file to be zipped
     * @param output stream where the output will be written
     */
    public static void zip(File file, OutputStream output) throws IOException {
        notNull(file, "file");
        notNull(output, "output");

        try (ZipOutputStream zos = new ZipOutputStream(output)) {
            final Path rootPath = file.getParentFile().toPath();
            if (file.isDirectory()) {
                zipDir(rootPath, file, zos);
            } else {
                zipFile(rootPath, file, zos);
            }

        }
    }

    private static void zipDir(Path rootPath, File dir, ZipOutputStream zos) throws IOException {
        for (File file : notNull(dir.listFiles(), "listed files")) {
            if (file.isDirectory()) {
                zipDir(rootPath, file, zos);
            } else {
                zipFile(rootPath, file, zos);
            }
        }
    }

    private static void zipFile(Path rootPath, File file, ZipOutputStream zos) throws IOException {
        ZipEntry ze = new ZipEntry(rootPath.relativize(file.toPath()).toString());
        zos.putNextEntry(ze);
        try (FileInputStream fis = new FileInputStream(file)) {
            StreamUtils.copy(fis, zos);
        }
        zos.closeEntry();
    }
}
