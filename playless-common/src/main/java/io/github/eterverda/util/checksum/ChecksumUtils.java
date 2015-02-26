package io.github.eterverda.util.checksum;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.eterverda.util.checksum.Checksum.ALGORITHM_SHA_1;

public final class ChecksumUtils {
    public static final String SHORT_ALGORITHM_SHA1 = "sha1";

    private static AtomicReference<byte[]> BUF = new AtomicReference<>();
    private static AtomicReference<MessageDigest> DIGEST = new AtomicReference<>();

    private ChecksumUtils() {
    }

    @NotNull
    public static byte[] digest(@NotNull String algorithm, @NotNull byte[] data) throws NoSuchAlgorithmException {
        final MessageDigest digest = obtainDigest(algorithm);

        final int length = digest.getDigestLength();
        byte[] value = new byte[length];

        try {
            digest.update(data);
            digest.digest(value, 0, length);

        } catch (DigestException ignore) {
            throw new AssertionError(ignore);
        }

        releaseDigest(digest);
        return value;
    }

    @NotNull
    public static byte[] digest(@NotNull String algorithm, @NotNull InputStream in) throws NoSuchAlgorithmException, IOException {
        final MessageDigest digest = obtainDigest(algorithm);

        final int length = digest.getDigestLength();
        byte[] value = new byte[length];

        final byte[] buf = obtainBuf();

        int len;
        while ((len = in.read(buf)) != -1) {
            digest.update(buf, 0, len);
        }

        releaseBuf(buf);

        try {
            digest.digest(value, 0, length);

        } catch (DigestException ignore) {
            throw new AssertionError(ignore);
        }

        releaseDigest(digest);
        return value;
    }

    @NotNull
    public static String bytesToHex(@NotNull byte[] bytes) {
        final byte[] buf = obtainBuf();

        final int length = bytes.length;
        for (int i = 0, j = 0; i < length; i++) {
            final byte b = bytes[i];
            final int b1 = (0xf & b >> 4);
            final int b2 = (0xf & b);
            buf[j++] = b1 <= 0x9 ? (byte) ('0' + b1) : (byte) ('a' + b1 - 0xa);
            buf[j++] = b2 <= 0x9 ? (byte) ('0' + b2) : (byte) ('a' + b2 - 0xa);
        }
        final String hexString = new String(buf, 0, length * 2);

        releaseBuf(buf);

        return hexString;
    }

    @NotNull
    protected static byte[] xor(@NotNull byte[] a, @NotNull byte[] b) {
        final int length = a.length;
        final byte[] c = new byte[length];
        for (int i = 0; i < length; i++) {
            final int ai = a[i];
            final int bi = b[i];
            final int ci = ai ^ bi;
            c[i] = (byte) (0xff & ci);
        }
        return c;
    }

    @NotNull
    protected static String shortAlgorithm(@NotNull String algorithm) {
        switch (algorithm) {
            case ALGORITHM_SHA_1:
            case SHORT_ALGORITHM_SHA1:
                return SHORT_ALGORITHM_SHA1;

            default:
                return algorithm.toLowerCase(Locale.US);
        }
    }

    @NotNull
    protected static String canonicalAlgorithm(@NotNull String algorithm) throws NoSuchAlgorithmException {
        switch (algorithm) {
            case ALGORITHM_SHA_1:
            case SHORT_ALGORITHM_SHA1:
                return ALGORITHM_SHA_1;

            default:
                final MessageDigest digest = obtainDigest(algorithm);
                final String canonicalAlgorithm = digest.getAlgorithm();
                releaseDigest(digest);
                return canonicalAlgorithm;
        }
    }

    @NotNull
    private static byte[] obtainBuf() {
        final byte[] oldBuf = BUF.getAndSet(null);
        return oldBuf != null ? oldBuf : new byte[8192];
    }

    @NotNull
    private static MessageDigest obtainDigest(String algorithm) throws NoSuchAlgorithmException {
        final MessageDigest oldDigest = DIGEST.getAndSet(null);
        return oldDigest != null && oldDigest.getAlgorithm().equals(algorithm) ? oldDigest : MessageDigest.getInstance(algorithm);
    }

    private static void releaseBuf(@Nullable byte[] buf) {
        BUF.set(buf);
    }

    private static void releaseDigest(@Nullable MessageDigest digest) {
        DIGEST.set(digest);
    }
}
