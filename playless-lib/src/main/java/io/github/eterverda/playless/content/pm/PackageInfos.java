package io.github.eterverda.playless.content.pm;

import android.content.pm.PackageInfo;
import android.content.pm.Signature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import io.github.eterverda.playless.common.util.DistributionFactories;
import io.github.eterverda.util.checksum.Checksum;

@SuppressWarnings("SpellCheckingInspection")
public class PackageInfos {
    private PackageInfos() {
    }

    @NotNull
    public static Checksum loadSignatures(@NotNull PackageInfo info) {
        if (info.signatures == null) {
            throw new IllegalArgumentException("No signatures in package info. Did you forget PackageManager.GET_SIGNATURES?");
        }
        return loadSignatures(info.signatures);
    }

    @NotNull
    private static Checksum loadSignatures(@NotNull Signature... certificates) {
        if (certificates.length == 0) {
            throw new AssertionError("How is it possible to have unsigned package?");
        }
        Checksum signatures = null;
        for (final Signature certificate : certificates) {
            final byte[] bytes = certificate.toByteArray();
            final Checksum signature = Checksum.sha1(bytes);
            signatures = Checksum.xor(signatures, signature);
        }
        assert signatures != null;
        return signatures;
    }

    @Nullable
    public static Checksum loadFingerprint(@NotNull PackageInfo info) {
        try {
            final File file = new File(info.applicationInfo.sourceDir);
            return DistributionFactories.loadFingerprint(file);

        } catch (IOException e) {
            return null;
        }
    }
}
