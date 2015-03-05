package io.github.eterverda.playless.core.io;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class Streams {
    private static final byte[] NULL = new byte[8192];

    private Streams() {
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public static void consume(@NotNull InputStream in) throws IOException {
        while (in.read(NULL) != -1) {
        }
    }
}
