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
}
