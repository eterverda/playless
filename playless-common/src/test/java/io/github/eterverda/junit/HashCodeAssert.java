package io.github.eterverda.junit;

import org.junit.Assert;

public final class HashCodeAssert {
    public static void assertEqualsHashCode(Object expected, Object actual) {
        assertEqualsHashCode(null, expected, actual);
    }

    public static void assertEqualsHashCode(String message, Object expected, Object actual) {
        Assert.assertEquals(message, expected, actual);
        Assert.assertEquals("Unequal hashCodes for equal objects", expected.hashCode(), actual.hashCode());
    }
}
