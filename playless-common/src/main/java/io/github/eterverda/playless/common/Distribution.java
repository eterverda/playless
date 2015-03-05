package io.github.eterverda.playless.common;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.eterverda.util.checksum.Checksum;

public final class Distribution {
    public static final String META_APP = "app";
    public static final String META_VERSION_NAME = "versionName";
    public static final String META_ICON = "icon";
    public static final String META_LABEL = "label";

    @NotNull
    public final String applicationId;
    @NotNull
    public final Version version;
    @NotNull
    public final Filter filter;
    @NotNull
    public final Map<String, String> meta;
    @NotNull
    public final Map<String, String> internalMeta;
    @NotNull
    public final Map<String, String> externalMeta;

    private Distribution(
            @NotNull String applicationId,
            @NotNull Version version,
            @NotNull Filter filter,
            @NotNull Map<String, String> meta,
            @NotNull Map<String, String> internalMeta,
            @NotNull Map<String, String> externalMeta) {

        this.applicationId = applicationId;
        this.version = version;
        this.filter = filter;
        this.meta = meta;
        this.internalMeta = internalMeta;
        this.externalMeta = externalMeta;
    }

    public static final class Version {
        public final int versionCode;
        public final long timestamp;
        @Nullable
        public final Checksum fingerprint;
        @Nullable
        public final Checksum signatures;
        public final boolean debug;

        private Version(int versionCode, long timestamp,
                        @Nullable Checksum fingerprint, @Nullable Checksum signatures,
                        boolean debug) {

            this.versionCode = versionCode;
            this.timestamp = timestamp;
            this.fingerprint = fingerprint;
            this.signatures = signatures;
            this.debug = debug;
        }
    }

    public static final class Filter {
        public final int minSdkVersion;
        public final int maxSdkVersion;
        public final Collection<String> supportsScreens;
        public final Collection<String> compatibleScreens;
        public final Collection<String> supportsGlTextures;
        public final Collection<String> abis;
        public final Collection<String> usesFeatures;
        public final Collection<String> usesConfigurations;
        public final Collection<String> usesLibraries;

        private Filter(
                int minSdkVersion, int maxSdkVersion,
                @NotNull Collection<String> supportsScreens,
                @NotNull Collection<String> compatibleScreens,
                @NotNull Collection<String> supportsGlTextures,
                @NotNull Collection<String> abis,
                @NotNull Collection<String> usesFeatures,
                @NotNull Collection<String> usesLibraries,
                @NotNull Collection<String> usesConfigurations) {

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

        @Override
        public int hashCode() {
            int h = 0;

            h ^= 0xffff0000 & minSdkVersion << 16;
            h ^= 0x0000ffff & maxSdkVersion;

            h ^= hashCode(supportsGlTextures);
            h ^= hashCode(supportsScreens);
            h ^= hashCode(compatibleScreens);
            h ^= hashCode(usesFeatures);
            h ^= hashCode(abis);
            h ^= hashCode(usesLibraries);
            h ^= hashCode(usesConfigurations);

            return h;
        }

        private int hashCode(Collection<?> collection) {
            int h = 0;
            for (Object e : collection) {
                h ^= e.hashCode();
            }
            return h;
        }
    }

    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor {
        private String applicationId;

        private int versionCode = 0;
        private long timestamp = Long.MIN_VALUE;
        private Checksum fingerprint;
        private Checksum signatures;
        private boolean debug;

        private int minSdkVersion = 1;
        private int maxSdkVersion = Integer.MAX_VALUE;
        private Collection<String> supportsScreens;
        private Collection<String> compatibleScreens;
        private Collection<String> supportsGlTextures;
        private Collection<String> abis;
        private Collection<String> usesFeatures;
        private Collection<String> usesConfigurations;
        private Collection<String> usesLibraries;

        private Map<String, String> meta;
        private Map<String, String> internalMeta;
        private Map<String, String> externalMeta;

        private boolean shared;

        public Editor() {
            supportsScreens = new TreeSet<>();
            compatibleScreens = new TreeSet<>();
            supportsGlTextures = new TreeSet<>();
            abis = new TreeSet<>();
            usesFeatures = new TreeSet<>();
            usesConfigurations = new TreeSet<>();
            usesLibraries = new TreeSet<>();

            meta = new TreeMap<>();
            internalMeta = new TreeMap<>();
            externalMeta = new TreeMap<>();
        }

        private Editor(Distribution dist) {
            applicationId = dist.applicationId;

            versionCode = dist.version.versionCode;
            timestamp = dist.version.timestamp;
            fingerprint = dist.version.fingerprint;
            signatures = dist.version.signatures;
            debug = dist.version.debug;

            minSdkVersion = dist.filter.minSdkVersion;
            maxSdkVersion = dist.filter.maxSdkVersion;
            supportsScreens = dist.filter.supportsScreens;
            compatibleScreens = dist.filter.compatibleScreens;
            supportsGlTextures = dist.filter.supportsGlTextures;
            abis = dist.filter.abis;
            usesFeatures = dist.filter.usesFeatures;
            usesConfigurations = dist.filter.usesConfigurations;
            usesLibraries = dist.filter.usesLibraries;

            meta = dist.meta;
            internalMeta = dist.internalMeta;
            externalMeta = dist.externalMeta;

            shared = true;
        }

        public Editor applicationId(String applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Editor versionCode(int versionCode) {
            this.versionCode = versionCode;
            return this;
        }

        public Editor timestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Editor fingerprint(Checksum fingerprint) {
            this.fingerprint = fingerprint;
            return this;
        }

        public Editor signature(Checksum signature) {
            return signatures(Checksum.xor(signatures, signature));
        }

        public Editor signatures(Checksum signatures) {
            this.signatures = signatures;
            return this;
        }

        public Editor debug(boolean debug) {
            this.debug = debug;
            return this;
        }

        public Editor minSdkVersion(int minSdkVersion) {
            this.minSdkVersion = minSdkVersion;
            return this;
        }

        public Editor maxSdkVersion(int maxSdkVersion) {
            this.maxSdkVersion = maxSdkVersion;
            return this;
        }

        public Editor supportsScreen(String... supportsScreens) {
            unShare();
            Collections.addAll(this.supportsScreens, supportsScreens);
            return this;
        }

        public Editor compatibleScreen(String... compatibleScreens) {
            unShare();
            Collections.addAll(this.compatibleScreens, compatibleScreens);
            return this;
        }

        public Editor supportsGlTexture(String... supportsGlTextures) {
            unShare();
            Collections.addAll(this.supportsGlTextures, supportsGlTextures);
            return this;
        }

        public Editor abi(String... abis) {
            unShare();
            Collections.addAll(this.abis, abis);
            return this;
        }

        public Editor usesFeature(String... usesFeatures) {
            unShare();
            Collections.addAll(this.usesFeatures, usesFeatures);
            return this;
        }

        public Editor usesConfiguration(String... usesConfigurations) {
            unShare();
            Collections.addAll(this.usesConfigurations, usesConfigurations);
            return this;
        }

        public Editor usesLibrary(String... usesLibraries) {
            unShare();
            Collections.addAll(this.usesLibraries, usesLibraries);
            return this;
        }

        public Editor meta(String key, String value) {
            unShare();
            meta.put(key, value);
            internalMeta.remove(key);
            externalMeta.remove(key);
            return this;
        }

        public Editor internalMeta(String key, String value) {
            unShare();
            meta.remove(key);
            internalMeta.put(key, value);
            externalMeta.remove(key);
            return this;
        }

        public Editor externalMeta(String key, String value) {
            unShare();
            meta.remove(key);
            internalMeta.remove(key);
            externalMeta.put(key, value);
            return this;
        }

        public Distribution build() {
            share();

            return new Distribution(
                    applicationId,
                    buildVersion(),
                    buildFilter(),
                    meta,
                    internalMeta,
                    externalMeta);
        }

        public Version buildVersion() {
            return new Version(versionCode,
                    timestamp,
                    fingerprint,
                    signatures,
                    debug);
        }

        private Filter buildFilter() {
            share();

            return new Filter(
                    minSdkVersion, maxSdkVersion,
                    supportsScreens,
                    compatibleScreens,
                    supportsGlTextures,
                    abis,
                    usesFeatures,
                    usesLibraries,
                    usesConfigurations);
        }

        private void share() {
            if (shared) {
                return;
            }
            supportsScreens = unmodifiableCollection(supportsScreens);
            compatibleScreens = unmodifiableCollection(compatibleScreens);
            supportsGlTextures = unmodifiableCollection(supportsGlTextures);
            abis = unmodifiableCollection(abis);
            usesFeatures = unmodifiableCollection(usesFeatures);
            usesLibraries = unmodifiableCollection(usesLibraries);
            usesConfigurations = unmodifiableCollection(usesConfigurations);

            meta = unmodifiableMap(meta);
            internalMeta = unmodifiableMap(internalMeta);
            externalMeta = unmodifiableMap(externalMeta);

            shared = true;
        }

        private void unShare() {
            if (!shared) {
                return;
            }
            supportsScreens = new TreeSet<>(supportsScreens);
            compatibleScreens = new TreeSet<>(compatibleScreens);
            supportsGlTextures = new TreeSet<>(supportsGlTextures);
            abis = new TreeSet<>(abis);
            usesFeatures = new TreeSet<>(usesFeatures);
            usesConfigurations = new TreeSet<>(usesConfigurations);
            usesLibraries = new TreeSet<>(usesLibraries);

            meta = new TreeMap<>(meta);
            internalMeta = new TreeMap<>(internalMeta);
            externalMeta = new TreeMap<>(externalMeta);

            shared = false;
        }

        private static <T> Collection<T> unmodifiableCollection(Collection<T> collection) {
            if (collection.isEmpty()) {
                return Collections.emptySet();
            }
            return Collections.unmodifiableCollection(collection);
        }

        private static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
            if (map.isEmpty()) {
                return Collections.emptyMap();
            }
            return Collections.unmodifiableMap(map);
        }
    }
}
