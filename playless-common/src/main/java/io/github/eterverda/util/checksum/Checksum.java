package io.github.eterverda.util.checksum;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.ThreadSafe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static io.github.eterverda.util.checksum.ChecksumUtils.canonicalAlgorithm;
import static io.github.eterverda.util.checksum.ChecksumUtils.digest;
import static io.github.eterverda.util.checksum.ChecksumUtils.digestLength;
import static io.github.eterverda.util.checksum.ChecksumUtils.hexToBytes;

@Immutable
@ThreadSafe
public final class Checksum {
    public static final String ALGORITHM_SHA_1 = "SHA-1";

    @NotNull
    private final String algorithm;
    @NotNull
    private final byte[] value;

    @Nullable
    private transient String stringValue;

    @Nullable
    private transient String string;

    public Checksum(@NotNull String algorithm, @NotNull @SuppressWarnings("NullableProblems") String hexValue) throws NoSuchAlgorithmException {
        this.algorithm = canonicalAlgorithm(algorithm);

        stringValue = hexValue;
        value = hexToBytes(hexValue);

        if (value.length != digestLength(this.algorithm)) {
            throw new IllegalArgumentException("Unexpected digest length " + value.length + " for algorithm " + algorithm);
        }
    }

    private Checksum(@SuppressWarnings("unused") boolean safe, @NotNull String algorithm, @NotNull byte[] value) {
        this.algorithm = algorithm;
        this.value = value;
    }

    @Override
    public boolean equals(Object that) {
        return that instanceof Checksum && equals((Checksum) that);
    }

    public boolean equals(Checksum that) {
        return this == that || algorithm.equals(that.algorithm) && Arrays.equals(value, that.value);
    }

    @NotNull
    public String getAlgorithm() {
        return algorithm;
    }

    @NotNull
    public String getShortAlgorithm() {
        return ChecksumUtils.shortAlgorithm(this.algorithm);
    }

    public String getStringValue() {
        if (stringValue == null) {
            stringValue = ChecksumUtils.bytesToHex(value);
        }
        return stringValue;
    }

    @NotNull
    public byte[] getValue() {
        final byte[] value = this.value;
        final int length = value.length;
        final byte[] result = new byte[length];

        System.arraycopy(value, 0, result, 0, length);

        return result;
    }

    public int getValue(@NotNull byte[] buf, int off, int len) {
        final byte[] value = this.value;
        final int length = value.length;
        final int result = length < len ? length : len;

        System.arraycopy(value, 0, buf, off, result);

        return result;
    }

    @Override
    public int hashCode() {
        return (value[0] & 0xff) << 24 | (value[1] & 0xff) << 16 | (value[2] & 0xff) << 8 | (value[3] & 0xff);
    }

    public int length() {
        return value.length;
    }

    @Override
    public String toString() {
        if (string == null) {
            string = getShortAlgorithm() + ":" + getStringValue();
        }
        return string;
    }

    @NotNull
    public Checksum xor(Checksum that) {
        return xor(this, that);
    }

    public static Checksum make(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        return new Checksum(true, canonicalAlgorithm(algorithm), digest(algorithm, data));
    }

    public static Checksum make(String algorithm, InputStream in) throws NoSuchAlgorithmException, IOException {
        return new Checksum(true, canonicalAlgorithm(algorithm), digest(algorithm, in));
    }

    @NotNull
    public static Checksum sha1(@NotNull byte[] data) {
        try {
            return make(ALGORITHM_SHA_1, data);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-1 should be always available");
        }
    }

    @NotNull
    public static Checksum sha1(@NotNull InputStream in) throws IOException {
        try {
            return make(ALGORITHM_SHA_1, in);
        } catch (NoSuchAlgorithmException e) {
            throw new AssertionError("SHA-1 should be always available");
        }
    }

    public static Checksum xor(Checksum a, Checksum b) {
        if (a == null) {
            return b;
        }
        if (b == null) {
            return a;
        }
        if (!a.algorithm.equals(b.algorithm)) {
            throw new IllegalArgumentException("Different algorithms " + a.algorithm + " vs. " + b.algorithm);
        }
        assert a.value.length == b.value.length;
        return new Checksum(true, a.algorithm, ChecksumUtils.xor(a.value, b.value));
    }
}
