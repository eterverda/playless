package io.github.eterverda.playless.cli;

import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.Link;
import io.github.eterverda.playless.common.util.DistFactories;
import io.github.eterverda.playless.core.InitialDistFactory;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.playless.core.json.JsonDistDumper;
import io.github.eterverda.util.checksum.Checksum;

public class DumpCommand implements Command {
    private static final boolean POST_PROCESS = Boolean.valueOf("true");

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
            final Dist postProcess = POST_PROCESS ? postProcess(file, preProcess) : preProcess;
            final Dist playProcess = playful ? playProcess(postProcess) : postProcess;

            dists[i] = playProcess;
        }

        dumper.writeDecorated(dists);

        System.out.println();
    }

    private Dist postProcess(File file, Dist dist) throws IOException {
        final Dist.Editor editor = dist.edit();

        for (Link link : dist.links) {
            final String rel = link.rel;
            if (rel.startsWith(Dist.LINK_REL_ICON)) {
                editor.unlink(link);

                final ZipFile zip = new ZipFile(file);
                final String entryName = link.href.substring(link.href.lastIndexOf('!') + 2);
                final ZipEntry entry = zip.getEntry(entryName);

                editor.link(rel, checksum2urn(DistFactories.loadFingerprint(zip, entry)));

            } else if (rel.equals(Dist.LINK_REL_DOWNLOAD)) {
                editor.unlink(link);
                editor.link(rel, checksum2urn(dist.version.fingerprint));
            }
        }

        return editor.build();
    }

    @NotNull
    private String checksum2urn(Checksum checksum) {
        final Base32 base32 = new Base32();
        final byte[] value = checksum.getValue();
        final String encodedValue = base32.encodeToString(value).toLowerCase();
        return "urn:sha1:" + encodedValue;
    }

    private Dist playProcess(Dist dist) {
        final Dist.Editor editor = dist.edit();

        editor.timestamp(Long.MIN_VALUE);

        for (Link link : dist.links) {
            final String rel = link.rel;
            if (rel.equals(Dist.LINK_REL_DOWNLOAD)) {
                editor.unlink(link);
                editor.link(Dist.LINK_REL_STORE, "https://play.google.com/store/apps/details?id=" + dist.applicationId);

            } else if (rel.startsWith(Dist.LINK_REL_ICON)) {
                editor.unlink(link);
                // no link back
            }
        }

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
