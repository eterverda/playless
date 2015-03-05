package io.github.eterverda.playless.cli;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.playless.common.util.TimestampUtils;
import io.github.eterverda.playless.core.AaptDistributionExtractor;
import io.github.eterverda.playless.core.FileSystemDistributionExtractor;
import io.github.eterverda.playless.core.JsonDistributionDumper;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.util.checksum.ChecksumUtils;

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
            builder.externalMeta(Distribution.META_APP, file.getAbsolutePath());

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
        final String baseName = baseName(dist);

        editor.meta(Distribution.META_APP, baseName + ".apk");

        for (String key : dist.internalMeta.keySet()) {
            if (!key.startsWith(Distribution.META_ICON)) {
                continue;
            }
            editor.meta(key, baseName + "-" + key + ".png");
        }

        return editor.build();
    }

    @NotNull
    public static String baseName(Distribution dist) {
        final String applicationId = dist.applicationId;
        final String timestamp = TimestampUtils.zulu(dist.version.timestamp);
        final String selector = ChecksumUtils.intToHexString(dist.filter.hashCode()).substring(0, 7);
        final String debug = dist.version.debug ? "debug" : null;

        final StringBuilder result = new StringBuilder(applicationId.length() + 35);
        result.append(timestamp);
        result.append('-');
        result.append(applicationId);
        result.append('-');
        result.append(selector);
        if (debug != null) {
            result.append('-');
            result.append(debug);
        }
        return result.toString();
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
