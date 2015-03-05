package io.github.eterverda.playless.core.util.jar;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.github.eterverda.playless.core.io.Streams;
import io.github.eterverda.util.checksum.Checksum;

public class Jars {
    private Jars() {
    }

    @Nullable
    public static Checksum loadSignatures(@NotNull File file) throws IOException {
        final Certificate[] certificates;
        try (JarFile jar = new JarFile(file)) {
            final JarEntry androidManifest = jar.getJarEntry("AndroidManifest.xml");

            if (androidManifest == null) {
                throw new IllegalArgumentException("No AndroidManifest.xml in " + file);
            }

            try (InputStream in = jar.getInputStream(androidManifest)) {
                Streams.consume(in);
            }

            certificates = androidManifest.getCertificates();
        }

        if (certificates == null || certificates.length == 0) {
            return null;
        }

        try {
            Checksum signatures = null;
            for (Certificate certificate : certificates) {
                final Checksum signature = Checksum.sha1(certificate.getEncoded());
                signatures = Checksum.xor(signatures, signature);
            }
            return signatures;

        } catch (CertificateEncodingException e) {
            throw new AssertionError(e);
        }
    }
}
