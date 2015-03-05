package io.github.eterverda.playless;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.util.checksum.Checksum;

public final class InstalledDistributionExtractor {
    private final PackageManager packageManager;
    private final String packageName;

    public InstalledDistributionExtractor(Context context) {
        packageManager = context.getPackageManager();
        packageName = context.getPackageName();
    }

    public Distribution loadSelf() {
        try {
            return load(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public Distribution load(String packageName) throws PackageManager.NameNotFoundException {
        final @NotNull PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        final Distribution.Editor dist = new Distribution.Editor();

        dist.applicationId(packageName);

        dist.versionCode(info.versionCode);
        dist.timestamp(info.lastUpdateTime);
        dist.fingerprint(fingerprint(info));
        dist.signatures(signatures(info));
        dist.debug(debug(info));

        dist.meta(Distribution.META_VERSION_NAME, info.versionName);
        dist.meta(Distribution.META_LABEL, label(info));

        return dist.build();
    }

    private String label(PackageInfo info) {
        return info.applicationInfo.loadLabel(packageManager).toString();
    }

    private static Checksum signatures(PackageInfo info) {
        return signatures(info.signatures);
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
    private static Checksum fingerprint(PackageInfo info) {
        final File file = new File(info.applicationInfo.sourceDir);
        try (InputStream in = new FileInputStream(file)) {
            return Checksum.sha1(in);

        } catch (Exception e) {
            return null;
        }
    }

    private static boolean debug(PackageInfo info) {
        return (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }
}
