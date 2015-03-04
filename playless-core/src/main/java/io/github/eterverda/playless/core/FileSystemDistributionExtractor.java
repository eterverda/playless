package io.github.eterverda.playless.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.util.checksum.Checksum;

public class FileSystemDistributionExtractor {
    private static final byte[] NULL = new byte[8192];

    public void extract(Distribution.Builder dist, File file) throws IOException {
        timestamp(dist, file);
        fingerprint(dist, file);
        signatures(dist, file);
    }

    private void timestamp(Distribution.Builder builder, File arg) {
        builder.timestamp(arg.lastModified());
    }

    private static void fingerprint(Distribution.Builder dist, File arg) throws IOException {
        try (FileInputStream in = new FileInputStream(arg)) {
            final Checksum fingerprint = Checksum.sha1(in);
            dist.fingerprint(fingerprint);
        }
    }

    private static void signatures(Distribution.Builder dist, File file) throws IOException {
        try (JarFile jar = new JarFile(file)) {
            final JarEntry androidManifest = jar.getJarEntry("AndroidManifest.xml");

            try (InputStream in = jar.getInputStream(androidManifest)) {
                consume(in);
            }

            signatures(dist, androidManifest.getCertificates());
        }
    }

    private static void signatures(Distribution.Builder dist, Certificate[] certificates) {
        for (Certificate certificate : certificates) {
            signature(dist, certificate);
        }
    }

    private static void signature(Distribution.Builder dist, Certificate certificate) {
        try {
            final Checksum signature = Checksum.sha1(certificate.getEncoded());
            dist.signature(signature);
        } catch (CertificateEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static void consume(InputStream in) throws IOException {
        //noinspection StatementWithEmptyBody
        while (in.read(NULL) != -1) {
            // we just consume data
        }
    }
}
