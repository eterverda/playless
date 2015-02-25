package io.github.eterverda.playless;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class InstalledDistribution {
    private static final MessageDigest DIGEST;
    private static final byte[] BUF = new byte[8192];
    private static final char[] HEX;

    static {
        try {
            DIGEST = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-1 is always available");
        }
        HEX = new char[DIGEST.getDigestLength() * 2];
    }

    @NotNull
    public final String applicationId;
    public final int versionCode;
    @NotNull
    public final String versionName;
    public final boolean locked;
    @Nullable
    public final String checksum;
    public final boolean debug;

    private InstalledDistribution(
            @NotNull PackageInfo info,
            boolean locked,
            @Nullable String checksum) {

        this(
                info.packageName,
                info.versionCode,
                info.versionName,
                locked,
                checksum,
                (info.applicationInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0);
    }

    public InstalledDistribution(
            @NotNull String applicationId,
            int versionCode,
            @NotNull String versionName,
            boolean locked,
            @Nullable String checksum,
            boolean debug) {

        this.applicationId = applicationId;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.locked = locked;
        this.checksum = checksum;
        this.debug = debug;
    }

    public static InstalledDistribution load(PackageManager packageManager, String packageName) throws PackageManager.NameNotFoundException {
        final PackageInfo info = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
        final String checksum = checksum(new File(info.applicationInfo.sourceDir));
        final boolean locked = checksum != null;

        return new InstalledDistribution(
                info,
                locked,
                checksum);
    }

    @SuppressLint("NewApi")
    private static String checksum(File sourceFile) {
        try (InputStream in = new FileInputStream(sourceFile)) {
            final byte[] digest;
            synchronized (DIGEST) {
                DIGEST.reset();

                int len;
                while ((len = in.read(BUF)) != -1) {
                    DIGEST.update(BUF, 0, len);
                }
                digest = DIGEST.digest();
            }
            return toHexString(digest);

        } catch (Exception e) {
            return null;
        }
    }

    private static String toHexString(byte[] bytes) {
        synchronized (HEX) {
            final int length = bytes.length;
            for (int i = 0, j = 0; i < length; i++) {
                final byte b = bytes[i];
                final int b1 = (0xf & b >> 4);
                final int b2 = (0xf & b);
                HEX[j++] = b1 <= 0x9 ? (char) ('0' + b1) : (char) ('a' + b1 - 0xa);
                HEX[j++] = b2 <= 0x9 ? (char) ('0' + b2) : (char) ('a' + b2 - 0xa);
            }
            return new String(HEX, 0, length * 2);
        }
    }

}
