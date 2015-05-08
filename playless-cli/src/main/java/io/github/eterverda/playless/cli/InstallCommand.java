package io.github.eterverda.playless.cli;

import org.apache.commons.codec.binary.Base32;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

import io.github.eterverda.playless.common.util.DistFactories;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.util.checksum.Checksum;

public class InstallCommand implements Command {
    @Override
    public void main(Repository repo, String[] args) {
        for (String arg : args) {
            try {
                install(arg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void install(String arg) throws IOException {
        final File file = new File(arg);
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
