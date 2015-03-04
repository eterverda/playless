package io.github.eterverda.playless.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.playless.core.AaptDistributionExtractor;
import io.github.eterverda.playless.core.FileSystemDistributionExtractor;
import io.github.eterverda.playless.core.JsonDistributionDumper;
import io.github.eterverda.playless.core.Repository;

public class DumpCommand implements Command {
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
        final String aapt = extractAapt(args);

        for (String arg : args) {
            final File file = new File(arg);
            final Distribution.Builder builder = new Distribution.Builder();

            System.out.printf("src : \"%s\",\n", file);

            final AaptDistributionExtractor aaptExtractor = new AaptDistributionExtractor(aapt);
            aaptExtractor.extract(builder, file);

            final FileSystemDistributionExtractor fsExtractor = new FileSystemDistributionExtractor();
            fsExtractor.extract(builder, file);

            final Distribution dist = builder.build();

            System.out.printf("apk : \"%s.apk\",\n", dist.baseName());
            System.out.printf("dst : ");

            final JsonDistributionDumper dumper = new JsonDistributionDumper(System.out);
            dumper.write(dist);

            System.out.println();
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
