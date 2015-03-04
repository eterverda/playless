package io.github.eterverda.playless.common;

import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.eterverda.playless.common.util.TimestampUtils;
import io.github.eterverda.util.checksum.Checksum;
import io.github.eterverda.util.checksum.ChecksumUtils;

public final class Distribution {
    @NotNull
    private final Apk apk;
    @NotNull
    private final Map<String, String> meta;
    @NotNull
    private final Requirements requirements;

    private transient String baseName;

    private Distribution(
            @NotNull Apk apk,
            @NotNull Map<String, String> meta,
            @NotNull Requirements requirements) {

        this.apk = apk;
        this.meta = meta;
        this.requirements = requirements;
    }

    @NotNull
    public Apk apk() {
        return apk;
    }

    @NotNull
    public Map<String, String> meta() {
        return meta;
    }

    @NotNull
    public Requirements requirements() {
        return requirements;
    }

    @NotNull
    public String baseName() {
        if (baseName != null) {
            return baseName;
        }
        final StringBuilder baseNameBuilder = new StringBuilder(apk.applicationId().length() + 35);
        baseNameBuilder.append(TimestampUtils.zulu(apk.timestamp()));
        baseNameBuilder.append('-');
        baseNameBuilder.append(apk.applicationId());
        baseNameBuilder.append('-');
        baseNameBuilder.append(requirements.selector());
        if (apk.debug()) {
            baseNameBuilder.append("-debug");
        }
        return baseName = baseNameBuilder.toString();
    }

    public static final class Requirements {
        private final int minSdkVersion;
        private final int maxSdkVersion;
        private final SortedSet<String> supportsScreens;
        private final SortedSet<String> compatibleScreens;
        private final SortedSet<String> supportsGlTextures;
        private final SortedSet<String> abis;
        private final SortedSet<String> usesFeatures;
        private final SortedSet<String> usesConfigurations;
        private final SortedSet<String> usesLibraries;

        private transient String selector;

        private Requirements(
                int minSdkVersion, int maxSdkVersion,
                @NotNull SortedSet<String> supportsScreens,
                @NotNull SortedSet<String> compatibleScreens,
                @NotNull SortedSet<String> supportsGlTextures,
                @NotNull SortedSet<String> abis,
                @NotNull SortedSet<String> usesFeatures,
                @NotNull SortedSet<String> usesLibraries,
                @NotNull SortedSet<String> usesConfigurations) {

            this.minSdkVersion = minSdkVersion;
            this.maxSdkVersion = maxSdkVersion;
            this.supportsScreens = supportsScreens;
            this.compatibleScreens = compatibleScreens;
            this.supportsGlTextures = supportsGlTextures;
            this.abis = abis;
            this.usesFeatures = usesFeatures;
            this.usesLibraries = usesLibraries;
            this.usesConfigurations = usesConfigurations;
        }

        public int minSdkVersion() {
            return minSdkVersion;
        }

        public int maxSdkVersion() {
            return maxSdkVersion;
        }

        public Collection<String> supportsScreens() {
            return supportsScreens;
        }

        public Collection<String> compatibleScreens() {
            return compatibleScreens;
        }

        public Collection<String> supportsGlTextures() {
            return supportsGlTextures;
        }

        public Collection<String> abis() {
            return abis;
        }

        public Collection<String> usesFeatures() {
            return usesFeatures;
        }

        public Collection<String> usesConfigurations() {
            return usesConfigurations;
        }

        public Collection<String> usesLibraries() {
            return usesLibraries;
        }

        public String selector() {
            if (selector != null) {
                return selector;
            }

            final StringBuilder selectorBuilder = new StringBuilder();

            selectorBuilder.append(minSdkVersion);
            selectorBuilder.append(maxSdkVersion);

            for (String supportsGlTexture : supportsGlTextures) {
                selectorBuilder.append(supportsGlTexture);
            }
            for (String supportsScreen : supportsScreens) {
                selectorBuilder.append(supportsScreen);
            }
            for (String compatibleScreen : compatibleScreens) {
                selectorBuilder.append(compatibleScreen);
            }
            for (String usesFeature : usesFeatures) {
                selectorBuilder.append(usesFeature);
            }
            for (String abi : abis) {
                selectorBuilder.append(abi);
            }
            for (String usesLibrary : usesLibraries) {
                selectorBuilder.append(usesLibrary);
            }
            for (String usesConfiguration : usesConfigurations) {
                selectorBuilder.append(usesConfiguration);
            }

            final Charset charset = Charset.forName("ISO-8859-1");

            final byte[] selectorBuilderBytes = selectorBuilder.toString().getBytes(charset);
            final byte[] selectorBytes = ChecksumUtils.sha1(selectorBuilderBytes);
            final String selectorHex = ChecksumUtils.bytesToHex(selectorBytes);

            return selector = selectorHex.substring(0, 13);
        }
    }

    public static final class Builder {
        private String applicationId;
        private int versionCode = 0;
        private String versionName;
        private long timestamp = Long.MIN_VALUE;
        private Checksum fingerprint;
        private Checksum signatures;
        private boolean debug;

        private SortedMap<String, String> meta = new TreeMap<>();

        private int minSdkVersion = 1;
        private int maxSdkVersion = Integer.MAX_VALUE;
        private SortedSet<String> supportsScreens = new TreeSet<>();
        private SortedSet<String> compatibleScreens = new TreeSet<>();
        private SortedSet<String> supportsGlTextures = new TreeSet<>();
        private SortedSet<String> abis = new TreeSet<>();
        private SortedSet<String> usesFeatures = new TreeSet<>();
        private SortedSet<String> usesConfigurations = new TreeSet<>();
        private SortedSet<String> usesLibraries = new TreeSet<>();

        private boolean shared;

        public Builder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder versionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Builder versionName(String versionName) {
            this.versionName = versionName;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder fingerprint(Checksum fingerprint) {
            this.fingerprint = fingerprint;
            return this;
        }

        public Builder signature(Checksum signature) {
            return signatures(Checksum.xor(signatures, signature));
        }

        public Builder signatures(Checksum signatures) {
            this.signatures = signatures;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Builder meta(String key, String value) {
            unShare();
            this.meta.put(key, value);
            return this;
        }

        public Builder minSdkVersion(int minSdkVersion) {
            this.minSdkVersion = minSdkVersion;
            return this;
        }

        public Builder maxSdkVersion(int maxSdkVersion) {
            this.maxSdkVersion = maxSdkVersion;
            return this;
        }

        public Builder supportsScreen(String... supportsScreens) {
            unShare();
            Collections.addAll(this.supportsScreens, supportsScreens);
            return this;
        }

        public Builder compatibleScreen(String... compatibleScreens) {
            unShare();
            Collections.addAll(this.compatibleScreens, compatibleScreens);
            return this;
        }

        public Builder supportsGlTexture(String... supportsGlTextures) {
            unShare();
            Collections.addAll(this.supportsGlTextures, supportsGlTextures);
            return this;
        }

        public Builder abi(String... abis) {
            unShare();
            Collections.addAll(this.abis, abis);
            return this;
        }

        public Builder usesFeature(String... usesFeatures) {
            unShare();
            Collections.addAll(this.usesFeatures, usesFeatures);
            return this;
        }

        public Builder usesConfiguration(String... usesConfigurations) {
            unShare();
            Collections.addAll(this.usesConfigurations, usesConfigurations);
            return this;
        }

        public Builder usesLibrary(String... usesLibraries) {
            unShare();
            Collections.addAll(this.usesLibraries, usesLibraries);
            return this;
        }

        public Distribution build() {
            shared = true;
            return new Distribution(
                    new Apk(applicationId,
                            versionCode,
                            versionName,
                            timestamp,
                            fingerprint,
                            signatures,
                            debug),
                    Collections.unmodifiableMap(meta),
                    new Requirements(
                            minSdkVersion, maxSdkVersion,
                            Collections.unmodifiableSortedSet(supportsScreens),
                            Collections.unmodifiableSortedSet(compatibleScreens),
                            Collections.unmodifiableSortedSet(supportsGlTextures),
                            Collections.unmodifiableSortedSet(abis),
                            Collections.unmodifiableSortedSet(usesFeatures),
                            Collections.unmodifiableSortedSet(usesLibraries),
                            Collections.unmodifiableSortedSet(usesConfigurations)));
        }

        private void unShare() {
            if (!shared) {
                return;
            }
            meta = new TreeMap<>(meta);
            supportsScreens = new TreeSet<>(supportsScreens);
            compatibleScreens = new TreeSet<>(compatibleScreens);
            supportsGlTextures = new TreeSet<>(supportsGlTextures);
            abis = new TreeSet<>(abis);
            usesFeatures = new TreeSet<>(usesFeatures);
            usesConfigurations = new TreeSet<>(usesConfigurations);
            usesLibraries = new TreeSet<>(usesLibraries);
        }
    }
}
