package io.github.eterverda.playless.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.core.DistNaming;
import io.github.eterverda.playless.core.InitialDistFactory;
import io.github.eterverda.playless.core.json.JsonDistDumper;
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
        final String base = DistNaming.base(dist);

        editor.meta(Dist.META_APP, base + ".apk");

        for (String key : dist.meta.keySet()) {
            if (!key.startsWith(Dist.META_ICON)) {
                continue;
            }
            editor.meta(key, base + "-" + key + ".png");
        }

        return editor.build();
    }

    private Dist playProcess(Dist dist) {
        final Dist.Editor editor = dist.edit();

        editor.timestamp(Long.MIN_VALUE);

        for (String key : dist.meta.keySet()) {
            if (key.startsWith(Dist.META_ICON)) {
                editor.meta(key, null);
            }
        }

        editor.meta(Dist.META_APP, "https://play.google.com/store/apps/details?id=" + dist.applicationId);

        return editor.build();
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
