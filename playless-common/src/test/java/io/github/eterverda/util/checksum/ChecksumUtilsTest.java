package io.github.eterverda.util.checksum;

import org.junit.Assert;
import org.junit.Test;

public class ChecksumUtilsTest {
    @Test
    public void intBytesToHex_00000000() {
        Assert.assertEquals("00000000", ChecksumUtils.intToHexString(0x00000000));
    }

    @Test
    public void intBytesToHex_cafebabe() {
        Assert.assertEquals("cafebabe", ChecksumUtils.intToHexString(0xcafebabe));
    }

    @Test
    public void intBytesToHex_ffffffff() {
        Assert.assertEquals("ffffffff", ChecksumUtils.intToHexString(0xffffffff));
    }

    @Test
    public void hexToBytes_00000000() {
        Assert.assertArrayEquals(new byte[]{0x00, 0x00, 0x00, 0x00}, ChecksumUtils.hexToBytes("00000000"));
    }

    @Test
    public void hexToBytes_cafebabe() {
        Assert.assertArrayEquals(new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe}, ChecksumUtils.hexToBytes("cafebabe"));
    }

    @Test
    public void hexToBytes_CAFEBABE() {
        Assert.assertArrayEquals(new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe}, ChecksumUtils.hexToBytes("CAFEBABE"));
    }

    @Test
    public void hexToBytes_ffffffff() {
        Assert.assertArrayEquals(new byte[]{(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff}, ChecksumUtils.hexToBytes("ffffffff"));
    }
}
