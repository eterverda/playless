package io.github.eterverda.playless.common.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.eterverda.util.checksum.Checksum;

public final class DistFactories {
    private DistFactories() {
    }

    @NotNull
    public static Checksum loadFingerprint(@NotNull File file) throws IOException {
        final FileInputStream in = new FileInputStream(file);
        return checksum(in);
    }

    @NotNull
    public static Checksum loadFingerprint(@NotNull ZipFile file, @NotNull ZipEntry entry) throws IOException {
        final InputStream in = file.getInputStream(entry);
        return checksum(in);
    }

    @NotNull
    private static Checksum checksum(@NotNull InputStream in) throws IOException {
        try {
            return Checksum.sha1(in);

        } finally {
            try {
                in.close();
            } catch (IOException ignore) {
            }
        }
    }
}
