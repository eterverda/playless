package io.github.eterverda.util.checksum;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

import io.github.eterverda.junit.HashCodeAssert;

public class ChecksumTest {
    private static final String ALGORITHM_MD5 = "MD5";

    private static final byte[] EMPTY = {};
    private static final byte[] EMPTY_SHA_1 = {-38, 57, -93, -18, 94, 107, 75, 13, 50, 85, -65, -17, -107, 96, 24, -112, -81, -40, 7, 9};
    private static final String EMPTY_SHA_1_STRING = "sha1:da39a3ee5e6b4b0d3255bfef95601890afd80709";

    private static final byte[] SOME = {1, 2, 3, 4, 5, 6, 12, 29, 67, -121};
    private static final byte[] SOME_SHA_1 = {-101, 22, 103, 82, 1, -58, 96, 53, -36, -58, 19, 106, 69, -118, -118, 110, 19, -60, -128, 98};
    private static final String SOME_SHA_1_STRING = "sha1:9b16675201c66035dcc6136a458a8a6e13c48062";

    private static final byte[] EMPTY_XOR_SOME_SHA_1 = {65, 47, -60, -68, 95, -83, 43, 56, -18, -109, -84, -123, -48, -22, -110, -2, -68, 28, -121, 107};

    @Test
    public void testEmptyValue() {
        final Checksum checksum = Checksum.sha1(EMPTY);

        Assert.assertArrayEquals(EMPTY_SHA_1, checksum.getValue());
    }

    @Test
    public void testEmptyStreamValue() throws IOException {
        final Checksum checksum = Checksum.sha1(new ByteArrayInputStream(EMPTY));

        Assert.assertArrayEquals(EMPTY_SHA_1, checksum.getValue());
    }

    @Test
    public void testSomeValue() {
        final Checksum checksum = Checksum.sha1(SOME);

        Assert.assertArrayEquals(SOME_SHA_1, checksum.getValue());
    }

    @Test
    public void testSomeStreamValue() throws IOException {
        final Checksum checksum = Checksum.sha1(new ByteArrayInputStream(SOME));

        Assert.assertArrayEquals(SOME_SHA_1, checksum.getValue());
    }

    @Test
    public void testEmptyStreamEqualsEmptyByteArray() throws IOException {
        final Checksum stream = Checksum.sha1(new ByteArrayInputStream(EMPTY));
        final Checksum array = Checksum.sha1(EMPTY);

        Assert.assertEquals(stream, array);
    }

    @Test
    public void testSomeStreamEqualsSomeByteArray() throws IOException {
        final Checksum stream = Checksum.sha1(new ByteArrayInputStream(SOME));
        final Checksum array = Checksum.sha1(SOME);

        Assert.assertEquals(stream, array);
    }

    @Test
    public void testEmptyNotEqualsSome() {
        final Checksum empty = Checksum.sha1(EMPTY);
        final Checksum some = Checksum.sha1(SOME);

        Assert.assertNotEquals(empty, some);
    }

    @Test
    public void testEmptySha1NotEqualsEmptyMd5() throws NoSuchAlgorithmException {
        final Checksum sha1 = Checksum.sha1(EMPTY);
        final Checksum md5 = md5(EMPTY);

        Assert.assertNotEquals(sha1, md5);
    }

    @Test
    public void testSomeSha1NotEqualsSomeMd5() throws NoSuchAlgorithmException {
        final Checksum sha1 = Checksum.sha1(SOME);
        final Checksum md5 = md5(SOME);

        Assert.assertNotEquals(sha1, md5);
    }

    @Test
    public void testEmptyXorSome() {
        final Checksum empty = Checksum.sha1(EMPTY);
        final Checksum some = Checksum.sha1(SOME);
        final Checksum xor = empty.xor(some);

        Assert.assertEquals("SHA-1", xor.getAlgorithm());
        Assert.assertArrayEquals(EMPTY_XOR_SOME_SHA_1, xor.getValue());
    }

    @Test
    public void testEmptyXorSomeEqualsSomeXorEmpty() {
        final Checksum empty = Checksum.sha1(EMPTY);
        final Checksum some = Checksum.sha1(SOME);
        final Checksum emptyXorSome = empty.xor(some);
        final Checksum someXorEmpty = some.xor(empty);

        HashCodeAssert.assertEqualsHashCode(emptyXorSome, someXorEmpty);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySha1XorEmptyMd5Throws() throws NoSuchAlgorithmException {
        final Checksum sha1 = Checksum.sha1(EMPTY);
        final Checksum md5 = md5(EMPTY);

        sha1.xor(md5);
    }

    @Test
    public void testSelfXorSelfEqualsZero() {
        final Checksum empty = Checksum.sha1(EMPTY);
        final Checksum emptyXorSelf = empty.xor(empty);

        Assert.assertArrayEquals(new byte[empty.length()], emptyXorSelf.getValue());

        final Checksum some = Checksum.sha1(SOME);
        final Checksum someXorSelf = some.xor(some);

        Assert.assertArrayEquals(new byte[some.length()], someXorSelf.getValue());
    }

    @Test
    public void testEmptyToString() {
        final Checksum checksum = Checksum.sha1(EMPTY);

        Assert.assertEquals(EMPTY_SHA_1_STRING, checksum.toString());
    }

    @Test
    public void testSomeToString() {
        final Checksum checksum = Checksum.sha1(SOME);

        Assert.assertEquals(SOME_SHA_1_STRING, checksum.toString());
    }

    @NotNull
    private static Checksum md5(byte[] data) throws NoSuchAlgorithmException {
        return new Checksum(ALGORITHM_MD5, data);
    }

    @NotNull
    public static Checksum md5(InputStream in) throws IOException, NoSuchAlgorithmException {
        return new Checksum(ALGORITHM_MD5, in);
    }
}
