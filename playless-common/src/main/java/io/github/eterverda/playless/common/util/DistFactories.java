package io.github.eterverda.playless.common.util;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import io.github.eterverda.util.checksum.Checksum;

public final class DistFactories {
    private DistFactories() {
    }

    @NotNull
    public static Checksum loadFingerprint(@NotNull File file) throws IOException {
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            return Checksum.sha1(in);

        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignore) {
                }
            }
        }
    }
}
