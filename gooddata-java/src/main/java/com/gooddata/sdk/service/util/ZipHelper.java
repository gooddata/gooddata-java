/*
 * (C) 2023 GoodData Corporation.
 * This source code is licensed under the BSD-style license found in the
 * LICENSE.txt file in the root directory of this source tree.
 */
package com.gooddata.sdk.service.util;

import static com.gooddata.sdk.common.util.Validate.notNull;

import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Utility class for manipulating zip archives.
 */
public abstract class ZipHelper {

    /**
     * This method compresses the input file to zip format. If the given file is a directory, it recursively
     * packs the directory into the output. Not including given directory itself.
     * If the given file is already zipped, simply copies it into the output.
     *
     * @param file file to be zipped
     * @param output stream where the output will be written
     * @throws IOException if zip creation fails
     */
    public static void zip(File file, OutputStream output) throws IOException {
        zip(file, output, false);
    }

    /**
     * This method compresses the input file to zip format. If the given file is a directory, it recursively
     * packs the directory into the output. If the given file is already zipped, simply copies it into the output.
     *
     * @param file file to be zipped
     * @param output stream where the output will be written
     * @param includeRoot if root dir should be included
     * @throws IOException if zip creation fails
     */
    public static void zip(File file, OutputStream output, boolean includeRoot) throws IOException {
        notNull(file, "file");
        notNull(output, "output");

        if (isZipped(file)) {
            try (InputStream fis = Files.newInputStream(file.toPath())) {
                StreamUtils.copy(fis, output);
            }
        } else {
            try (ZipOutputStream zos = new ZipOutputStream(output)) {
                if (file.isDirectory()) {
                    zipDir(includeRoot ? file.getParentFile().toPath() : file.toPath(), file, zos);
                } else {
                    zipFile(file.getParentFile().toPath(), file, zos);
                }
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
        try (InputStream fis = Files.newInputStream(file.toPath())) {
            StreamUtils.copy(fis, zos);
        }
        zos.closeEntry();
    }

    private static boolean isZipped(File file) {
        try (final InputStream stream = Files.newInputStream(file.toPath());
             final ZipInputStream zipStream = new ZipInputStream(stream)) {
            return zipStream.getNextEntry() != null;
        } catch (IOException e) {
            return false;
        }
    }
}
