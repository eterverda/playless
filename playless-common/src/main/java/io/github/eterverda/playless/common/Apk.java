package io.github.eterverda.playless.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.github.eterverda.util.checksum.Checksum;

public final class Apk {
    @NotNull
    private final String applicationId;
    private final int versionCode;
    @NotNull
    private final String versionName;
    private final long timestamp;
    @Nullable
    private final Checksum fingerprint;
    @Nullable
    private final Checksum signatures;
    private final boolean debug;

    public Apk(
            @NotNull String applicationId,
            int versionCode, @NotNull String versionName, long timestamp,
            @Nullable Checksum fingerprint, @Nullable Checksum signatures,
            boolean debug) {

        this.applicationId = applicationId;
        this.versionCode = versionCode;
        this.versionName = versionName;
        this.timestamp = timestamp;
        this.fingerprint = fingerprint;
        this.signatures = signatures;
        this.debug = debug;
    }

    @NotNull
    public String applicationId() {
        return applicationId;
    }

    public int versionCode() {
        return versionCode;
    }

    @NotNull
    public String versionName() {
        return versionName;
    }

    public long timestamp() {
        return timestamp;
    }

    @Nullable
    public Checksum fingerprint() {
        return fingerprint;
    }

    @Nullable
    public Checksum signatures() {
        return signatures;
    }

    public boolean debug() {
        return debug;
    }
}
