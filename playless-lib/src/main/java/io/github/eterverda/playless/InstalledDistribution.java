package io.github.eterverda.playless;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import io.github.eterverda.util.checksum.Checksum;

public final class InstalledDistribution {
    @NotNull
    public final String applicationId;
    public final int versionCode;
    @NotNull
    public final String versionName;
    public final boolean locked;
    @Nullable
    public final Checksum fingerprint;
    @NotNull
    public final Checksum signatures;
    public final boolean debug;

    private InstalledDistribution(
            @NotNull PackageInfo info,
            boolean locked,
            @Nullable Checksum fingerprint,
            @NotNull Checksum signatures) {

        this(
                info.packageName,
                info.versionCode,
                info.versionName,
                locked,
                fingerprint,
                signatures,
                (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    public InstalledDistribution(
            @NotNull String applicationId,
            int versionCode,
            @NotNull String versionName,
            boolean locked,
            @Nullable Checksum fingerprint,
            @NotNull Checksum signatures,
            boolean debug) {

        this.applicationId = applicationId;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.locked = locked;
        this.fingerprint = fingerprint;
        this.signatures = signatures;
        this.debug = debug;
    }

    public static InstalledDistribution load(PackageManager packageManager, String packageName) throws PackageManager.NameNotFoundException {
        final @NotNull PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        final @Nullable Checksum fingerprint = fingerprint(new File(info.applicationInfo.sourceDir));
        final @NotNull Checksum signatures = signatures(info.signatures);
        final boolean locked = fingerprint != null;

        return new InstalledDistribution(
                info,
                locked,
                fingerprint,
                signatures);
    }

    private static Checksum signatures(Signature... signatures) {
        Checksum result = null;
        for (Signature signature : signatures) {
            result = Checksum.xor(result, signature(signature));
        }
        return result;
    }

    private static Checksum signature(Signature signature) {
        final byte[] bytes = signature.toByteArray();
        return Checksum.sha1(bytes);
    }

    @SuppressLint("NewApi")
    @Nullable
    private static Checksum fingerprint(@NotNull File sourceFile) {
        try (InputStream in = new FileInputStream(sourceFile)) {
            return Checksum.sha1(in);

        } catch (Exception e) {
            return null;
        }
    }
}
