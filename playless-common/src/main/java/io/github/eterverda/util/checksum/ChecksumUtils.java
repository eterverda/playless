package io.github.eterverda.util.checksum;

import net.jcip.annotations.ThreadSafe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static io.github.eterverda.util.checksum.Checksum.ALGORITHM_SHA_1;

@ThreadSafe
public final class ChecksumUtils {
    public static final String SHORT_ALGORITHM_SHA1 = "sha1";

    private static AtomicReference<byte[]> BUF = new AtomicReference<>();
    private static AtomicReference<MessageDigest> DIGEST = new AtomicReference<>();

    private ChecksumUtils() {
    }

    @NotNull
    public static byte[] digest(@NotNull String algorithm, @NotNull byte[] data) throws NoSuchAlgorithmException {
        final MessageDigest digest = obtainDigest(algorithm);

        digest.update(data);
        final byte[] result = digest.digest();

        releaseDigest(digest);
        return result;
    }

    @NotNull
    public static byte[] digest(@NotNull String algorithm, @NotNull InputStream in) throws NoSuchAlgorithmException, IOException {
        final MessageDigest digest = obtainDigest(algorithm);

        final byte[] buf = obtainBuf();

        int len;
        while ((len = in.read(buf)) != -1) {
            digest.update(buf, 0, len);
        }

        releaseBuf(buf);

        byte[] result = digest.digest();

        releaseDigest(digest);
        return result;
    }

    /**
     * Returns a string representation of the integer argument as an unsigned integer in
     * base&nbsp;16. Unlike {@link java.lang.Integer#toHexString(int)} result is zero-padded and
     * always has a length of 8.
     *
     * @param i an integer to be converted to a string.
     * @return the string representation of the unsigned integer value.
     * @see java.lang.Integer#toHexString(int)
     */
    @NotNull
    public static String intToHexString(int i) {
        final byte[] buf = obtainBuf();

        for (int j = 0, k = 28; j < 8; j++, k -= 4) {
            final int b = 0xf & i >> k;
            buf[j] = halfByteToHex(b);
        }

        final String hexString = new String(buf, 0, 8);

        releaseBuf(buf);

        return hexString;
    }

    @NotNull
    public static String bytesToHex(@NotNull byte[] bytes) {
        final byte[] buf = obtainBuf();

        final int length = bytes.length;
        for (int i = 0, j = 0; i < length; i++) {
            final byte b = bytes[i];
            final int b1 = (0xf & b >> 4);
            final int b2 = (0xf & b);
            buf[j++] = halfByteToHex(b1);
            buf[j++] = halfByteToHex(b2);
        }
        final String hexString = new String(buf, 0, length * 2);

        releaseBuf(buf);

        return hexString;
    }

    private static byte halfByteToHex(int b) {
        return b <= 0x9 ? (byte) ('0' + b) : (byte) ('a' + b - 0xa);
    }

    @NotNull
    public static byte[] hexToBytes(@NotNull String hex) {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException(hex);
        }
        final int length = hex.length() / 2;
        final byte[] result = new byte[length];

        for (int i = 0, j = 0; i < length; i++) {
            final char c1 = hex.charAt(j++);
            final char c2 = hex.charAt(j++);
            final int b1 = hexToHalfByte(c1);
            final int b2 = hexToHalfByte(c2);
            result[i] = (byte) (0xff & (b1 << 4 | b2));
        }
        return result;
    }

    private static int hexToHalfByte(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 0xa;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 0xa;
        }
        throw new IllegalArgumentException("Not a hex digit " + c);
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
