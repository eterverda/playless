package io.github.eterverda.playless.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public final class Distribution {
    private final String applicationId;
    private final int versionCode;
    private final long timestamp;
    private final String sha1;
    private final boolean debug;
    private final Selector selector;
    private final Filter filter;

    private Distribution(
            @NotNull String applicationId,
            int versionCode, long timestamp, @Nullable String sha1,
            boolean debug,
            @NotNull Selector selector, Filter filter) {

        this.applicationId = applicationId;
        this.versionCode = versionCode;
        this.timestamp = timestamp;
        this.sha1 = sha1;
        this.debug = debug;
        this.selector = selector;
        this.filter = filter;
    }

    public String applicationId() {
        return applicationId;
    }

    public int versionCode() {
        return versionCode;
    }

    public long timestamp() {
        return timestamp;
    }

    public String sha1() {
        return sha1;
    }

    public boolean debug() {
        return debug;
    }

    public Selector selector() {
        return selector;
    }

    public Filter filter() {
        return filter;
    }

    public static final class Selector {
        private final int minSdkVersion;
        private final int maxSdkVersion;
        private final SortedSet<String> supportsScreens;
        private final SortedSet<String> compatibleScreens;
        private final SortedSet<String> supportsGlTextures;
        private final SortedSet<String> abis;
        private final SortedSet<String> usesFeatures;

        private Selector(
                int minSdkVersion, int maxSdkVersion,
                @NotNull SortedSet<String> supportsScreens,
                @NotNull SortedSet<String> compatibleScreens,
                @NotNull SortedSet<String> supportsGlTextures,
                @NotNull SortedSet<String> abis,
                @NotNull SortedSet<String> usesFeatures) {

            this.minSdkVersion = minSdkVersion;
            this.maxSdkVersion = maxSdkVersion;
            this.supportsScreens = supportsScreens;
            this.compatibleScreens = compatibleScreens;
            this.supportsGlTextures = supportsGlTextures;
            this.abis = abis;
            this.usesFeatures = usesFeatures;
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
    }

    public static final class Filter {
        private final SortedSet<String> usesConfigurations;
        private final SortedSet<String> usesLibraries;

        private Filter(
                @NotNull SortedSet<String> usesLibraries,
                @NotNull SortedSet<String> usesConfigurations) {

            this.usesLibraries = usesLibraries;
            this.usesConfigurations = usesConfigurations;
        }

        public Collection<String> usesConfigurations() {
            return usesConfigurations;
        }

        public Collection<String> usesLibraries() {
            return usesLibraries;
        }
    }

    public static final class Builder {
        private String applicationId;
        private int versionCode = 0;
        private long timestamp = Long.MIN_VALUE;
        private String sha1;
        private boolean debug;

        private int minSdkVersion = 1;
        private int maxSdkVersion = Integer.MAX_VALUE;
        private final SortedSet<String> supportsScreens = new TreeSet<>();
        private final SortedSet<String> compatibleScreens = new TreeSet<>();
        private final SortedSet<String> supportsGlTextures = new TreeSet<>();
        private final SortedSet<String> abis = new TreeSet<>();
        private final SortedSet<String> usesFeatures = new TreeSet<>();

        private final SortedSet<String> usesConfigurations = new TreeSet<>();
        private final SortedSet<String> usesLibraries = new TreeSet<>();

        public Builder applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder versionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Builder timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder sha1(String sha1) {
            this.sha1 = sha1;
            return this;
        }

        public Builder debug(boolean debug) {
            this.debug = debug;
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

        public Builder supportsScreen(String supportsScreen) {
            this.supportsScreens.add(supportsScreen);
            return this;
        }

        public Builder compatibleScreen(String compatibleScreen) {
            this.compatibleScreens.add(compatibleScreen);
            return this;
        }

        public Builder supportsGlTexture(String supportsGlTexture) {
            this.supportsGlTextures.add(supportsGlTexture);
            return this;
        }

        public Builder abi(String abi) {
            this.abis.add(abi);
            return this;
        }

        public Builder usesFeature(String usesFeature) {
            this.usesFeatures.add(usesFeature);
            return this;
        }

        public Builder usesConfiguration(String usesConfiguration) {
            this.usesConfigurations.add(usesConfiguration);
            return this;
        }

        public Builder usesLibrary(String usesLibrary) {
            this.usesFeatures.add(usesLibrary);
            return this;
        }

        public Distribution build() {
            return new Distribution(
                    applicationId,
                    versionCode,
                    timestamp,
                    sha1,
                    debug,
                    new Selector(
                            minSdkVersion, maxSdkVersion,
                            Collections.unmodifiableSortedSet(supportsScreens),
                            Collections.unmodifiableSortedSet(compatibleScreens),
                            Collections.unmodifiableSortedSet(supportsGlTextures),
                            Collections.unmodifiableSortedSet(abis),
                            Collections.unmodifiableSortedSet(usesFeatures)),
                    new Filter(
                            Collections.unmodifiableSortedSet(usesConfigurations),
                            Collections.unmodifiableSortedSet(usesLibraries)));
        }
    }
}
