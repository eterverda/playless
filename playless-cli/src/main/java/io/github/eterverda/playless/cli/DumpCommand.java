package io.github.eterverda.playless.cli;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.util.TimestampUtils;
import io.github.eterverda.playless.core.InitialDistFactory;
import io.github.eterverda.playless.core.JsonDistDumper;
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
        final boolean pretty = extractFlag(args, "--pretty");
        final boolean playful = extractFlag(args, "--playful");

        final InitialDistFactory factory = new InitialDistFactory(aapt);

        final JsonDistDumper dumper = new JsonDistDumper(System.out);
        dumper.setPrettyPrint(pretty);

        final Dist[] dists = new Dist[args.size()];

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            final File file = new File(arg);

            final Dist preProcess = factory.load(file);
            final Dist postProcess = POST_PROCESS ? postProcess(preProcess) : preProcess;
            final Dist playProcess = playful ? playProcess(postProcess) : postProcess;

            dists[i] = playProcess;
        }

        dumper.write(dists);

        System.out.println();
    }

    private Dist postProcess(Dist dist) {
        final Dist.Editor editor = dist.edit();
        final String baseName = baseName(dist);

        editor.meta(Dist.META_APP, baseName + ".apk");

        for (String key : dist.meta.keySet()) {
            if (!key.startsWith(Dist.META_ICON)) {
                continue;
            }
            editor.meta(key, baseName + "-" + key + ".png");
        }

        return editor.build();
    }

    private Dist playProcess(Dist dist) {
        final Dist.Editor editor = dist.edit();

        editor.timestamp(Long.MIN_VALUE);

        for (String key : dist.meta.keySet()) {
            if (key.equals(Dist.META_APP) || key.startsWith(Dist.META_ICON)) {
                editor.meta(key, null);
            }
        }

        return editor.build();
    }

    @NotNull
    public static String baseName(Dist dist) {
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

    private boolean extractFlag(ArrayList<String> args, String flag) {
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            if (arg.equals(flag)) {
                args.remove(i);
                return true;
            }
        }
        return false;
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
