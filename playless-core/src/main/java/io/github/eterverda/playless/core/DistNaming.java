package io.github.eterverda.playless.core;

import org.jetbrains.annotations.NotNull;

import io.github.eterverda.playless.common.Dist;

public final class DistNaming {
    private static final String TIMESTAMP_FORMAT = "%1$tY%1$tm%1$td%1$tH%1$tM%1$tS"; // this format is locale independent
    private static final String SELECTOR_FORMAT = "%07x"; // this format is locale independent

    private DistNaming() {
    }

    @NotNull
    public static String base(@NotNull Dist dist) {
        final String tst = String.format(TIMESTAMP_FORMAT, dist.version.timestamp);
        final String app = dist.applicationId;
        final String ver = Integer.toString(dist.version.versionCode);
        final String sel = String.format(SELECTOR_FORMAT, dist.filter.hashCode());
        final String dbg = dist.version.debug ? "-debug" : "";

        return tst + '-' + app + '.' + ver + '-' + sel + dbg;
    }
}
