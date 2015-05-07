package io.github.eterverda.playless.cli;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import io.github.eterverda.playless.common.util.DistFactories;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.util.checksum.Checksum;

public class InstallCommand implements Command {
    public static Subparser addSubParser(ArgumentParser parser) {
        final Subparser subparser = parser.addSubparsers().addParser("install").help("installs artifact to playless repository");
        Main.addApksArgument(subparser);
        return subparser;
    }

    @Override
    public void main(Namespace args) {
        for (File file : args.<File>getList("apk")) {
            try {
                install(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void install(File file) throws IOException {
        final Checksum fingerprint = DistFactories.loadFingerprint(file);
        final String base32 = base32(fingerprint);
        System.out.printf("+ blob %s (file:%s)\n", base32, file.getAbsolutePath());
    }

    @NotNull
    static String base32(Checksum fingerprint) {
        final Base32 base32 = new Base32();
        final byte[] value = fingerprint.getValue();
        return base32.encodeToString(value).toLowerCase();
    }
}
