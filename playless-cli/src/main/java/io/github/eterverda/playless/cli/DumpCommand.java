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
    @SuppressWarnings("FieldCanBeLocal")
    private boolean POST_PROCESS = true;

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
        final JsonDistributionDumper dumper = new JsonDistributionDumper(System.out);

        for (String arg : args) {
            final File file = new File(arg);
            final Distribution.Editor builder = new Distribution.Editor();
            builder.externalMeta("app", file.getAbsolutePath());

            final AaptDistributionExtractor aaptExtractor = new AaptDistributionExtractor(aapt);
            aaptExtractor.extract(builder, file);

            final FileSystemDistributionExtractor fsExtractor = new FileSystemDistributionExtractor();
            fsExtractor.extract(builder, file);

            final Distribution preProcess = builder.build();
            final Distribution postProcess = POST_PROCESS ? postProcess(preProcess) : preProcess;

            dumper.write(postProcess);

            System.out.println();
        }
    }

    private Distribution postProcess(Distribution dist) {
        final Distribution.Editor editor = dist.edit();
        final String baseName = dist.baseName();

        editor.meta("app", baseName + ".apk");

        for (String key : dist.internalMeta().keySet()) {
            if (!key.startsWith("icon")) {
                continue;
            }
            editor.meta(key, baseName + "-" + key + ".png");
        }

        return editor.build();
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
