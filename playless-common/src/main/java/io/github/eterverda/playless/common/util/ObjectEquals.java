package io.github.eterverda.playless.common.util;

import org.jetbrains.annotations.Nullable;

public final class ObjectEquals {
    private ObjectEquals() {
    }

    /**
     * Same as {@link java.util.Objects#equals(Object, Object)} but available on older Androids.
     */
    public static boolean equals(@Nullable Object a, @Nullable Object b) {
        return a == null ? b == null : a.equals(b);
    }
}
