package io.github.eterverda.playless.cli;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.Link;
import io.github.eterverda.playless.common.Repo;
import io.github.eterverda.playless.common.util.DistFactories;
import io.github.eterverda.playless.core.InitialDistFactory;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.playless.core.json.JsonRepoDumper;
import io.github.eterverda.util.checksum.Checksum;

public class DumpCommand implements Command {
    private static final boolean POST_PROCESS = Boolean.valueOf("true");

    private static final String ICON_FILENAME = "icon.png";

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

        final JsonRepoDumper dumper = new JsonRepoDumper(System.out);
        dumper.setPrettyPrint(pretty);

        final Repo.Editor repo = new Repo.Editor();

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            final File file = new File(arg);

            final Dist preProcess = factory.load(file);
            final Dist postProcess = POST_PROCESS ? postProcess(preProcess) : preProcess;
            final Dist playProcess = playful ? playProcess(postProcess) : postProcess;

            repo.dist(playProcess);
        }

        dumper.writeDecorated(repo.build());

        System.out.println();
    }

    private Dist postProcess(Dist dist) throws IOException {
        final Dist.Editor editor = dist.edit();

        for (Link link : dist.links) {
            final String rel = link.rel();
            final String href = link.href();
            if (rel.startsWith(Dist.LINK_REL_ICON) && href.startsWith("zip:file:")) {
                editor.unlink(link);

                final int indexOfExclamation = href.lastIndexOf('!');

                final File file = new File(href.substring(9, indexOfExclamation));
                final String entryName = href.substring(indexOfExclamation + 2);

                final ZipFile zip = new ZipFile(file);
                final ZipEntry entry = zip.getEntry(entryName);

                final Checksum fingerprint = DistFactories.loadFingerprint(zip, entry);

                editor.link(rel, makeUrl(fingerprint, ICON_FILENAME));

            } else if (rel.equals(Dist.LINK_REL_DOWNLOAD)) {
                editor.unlink(link);

                final String applicationId = dist.applicationId;
                final String version = dist.meta.containsKey(Dist.META_VERSION_NAME) ?
                        dist.meta.get(Dist.META_VERSION_NAME) :
                        Integer.toString(dist.version.versionCode);

                final Checksum fingerprint = dist.version.fingerprint;
                final String filename = applicationId + "-" + version + ".apk";

                editor.link(rel, makeUrl(fingerprint, filename));
            }
        }

        return editor.build();
    }

    private Dist playProcess(Dist dist) {
        final Dist.Editor editor = dist.edit();

        editor.timestamp(Long.MIN_VALUE);

        for (Link link : dist.links) {
            final String rel = link.rel();
            if (rel.equals(Dist.LINK_REL_DOWNLOAD)) {
                editor.unlink(link);
                editor.link(Dist.LINK_REL_STORE, "https://play.google.com/store/apps/details?id=" + dist.applicationId);

            } else if (rel.startsWith(Dist.LINK_REL_ICON)) {
                editor.unlink(link);
                // no link back
            }
        }

        editor.unmeta(Dist.META_DOWNLOAD_SIZE);

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

    @NotNull
    private static String makeUrl(Checksum fingerprint, String filename) {
        final String encodedValue = InstallCommand.base32(fingerprint);
        return ".playless/" + encodedValue.substring(0, 2) + "/" + encodedValue.substring(2) + "/" + filename;
    }
}
