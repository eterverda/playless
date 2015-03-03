package io.github.eterverda.playless.cli;

import org.apache.commons.exec.CommandLine;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.playless.core.AaptDistributionExtractor;
import io.github.eterverda.playless.core.JsonDistributionDumper;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.util.checksum.Checksum;

public class DumpCommand implements Command {
    private static final byte[] NULL = new byte[8192];

    @Override
    public void main(Repository repo, String[] rawArgs) {
        final ArrayList<String> args = new ArrayList<>(rawArgs.length);
        Collections.addAll(args, rawArgs);

        try {
            main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void main(ArrayList<String> args) throws IOException {
        final CommandLine aapt = new CommandLine(extractAapt(args))
                .addArgument("dump")
                .addArgument("badging");

        for (String arg : args) {
            final Distribution.Builder builder = new Distribution.Builder();

            System.out.printf("src : \"%s\",\n", arg);

            timestamp(builder, arg);
            aapt(builder, aapt, arg);
            fingerprint(builder, arg);
            signatures(builder, arg);

            final Distribution dist = builder.build();

            System.out.printf("apk : \"%s\",\n", dist.apkName());
            System.out.printf("dst : ");

            final JsonDistributionDumper dumper = new JsonDistributionDumper(System.out);
            dumper.write(dist);

            System.out.println();
        }
    }

    private void timestamp(Distribution.Builder builder, String arg) {
        builder.timestamp(new File(arg).lastModified());
    }

    private static void aapt(Distribution.Builder dist, CommandLine aapt, String arg) throws IOException {
        final CommandLine line = new CommandLine(aapt).addArgument(arg);

        final AaptDistributionExtractor extractor = new AaptDistributionExtractor(line);
        extractor.extract(dist);
    }

    private static void fingerprint(Distribution.Builder dist, String arg) throws IOException {
        try (FileInputStream in = new FileInputStream(new File(arg))) {
            final Checksum fingerprint = Checksum.sha1(in);
            dist.fingerprint(fingerprint);
        }
    }

    private static void signatures(Distribution.Builder dist, String arg) throws IOException {
        try (JarFile jar = new JarFile(arg)) {
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

    private static String extractAapt(ArrayList<String> args) {
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            if (arg.startsWith("--aapt=")) {
                args.remove(i);
                return arg.substring(7);
            }
            if (arg.equals("--aapt")) {
                if (args.size() == i - 1) {
                    throw new IllegalArgumentException("No value for option " + arg);
                }
                args.remove(i);
                return args.remove(i);
            }
        }
        return null;
    }
}
