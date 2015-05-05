package io.github.eterverda.playless.common;

import org.jetbrains.annotations.NotNull;

public final class Link implements Comparable<Link> {
    private final String rel;
    private final String href;

    public Link(@NotNull String rel, @NotNull String href) {
        this.rel = rel;
        this.href = href;
    }

    @NotNull
    public String rel() {
        return rel;
    }

    @NotNull
    public String href() {
        return href;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Link && equals((Link) other);
    }

    public boolean equals(Link other) {
        return this == other || other != null && rel.equals(other.rel) && href.equals(other.href);
    }

    @Override
    public int hashCode() {
        return href.hashCode() + 31 * rel.hashCode();
    }

    @Override
    public int compareTo(Link other) {
        final int r = rel.compareTo(other.rel);
        if (r != 0) {
            return r;
        }
        return href.compareTo(other.href);
    }
}
