package io.github.eterverda.playless.common;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.eterverda.playless.common.util.TimestampUtils;
import io.github.eterverda.util.checksum.Checksum;
import io.github.eterverda.util.checksum.ChecksumUtils;

public final class Distribution {
    @NotNull
    private final Apk apk;
    @NotNull
    private final Requirements requirements;
    @NotNull
    private final Map<String, String> meta;
    @NotNull
    private final Map<String, String> internalMeta;
    @NotNull
    private final Map<String, String> externalMeta;

    private Distribution(
            @NotNull Apk apk,
            @NotNull Requirements requirements,
            @NotNull Map<String, String> meta,
            @NotNull Map<String, String> internalMeta,
            @NotNull Map<String, String> externalMeta) {

        this.apk = apk;
        this.requirements = requirements;
        this.meta = meta;
        this.internalMeta = internalMeta;
        this.externalMeta = externalMeta;
    }

    @NotNull
    public Apk apk() {
        return apk;
    }

    @NotNull
    public Requirements requirements() {
        return requirements;
    }

    @NotNull
    public Map<String, String> meta() {
        return meta;
    }

    @NotNull
    public Map<String, String> internalMeta() {
        return internalMeta;
    }

    @NotNull
    public Map<String, String> externalMeta() {
        return externalMeta;
    }

    @NotNull
    public String baseName() {
        final StringBuilder baseNameBuilder = new StringBuilder(apk.applicationId().length() + 35);
        baseNameBuilder.append(TimestampUtils.zulu(apk.timestamp()));
        baseNameBuilder.append('-');
        baseNameBuilder.append(apk.applicationId());
        baseNameBuilder.append('-');
        baseNameBuilder.append(requirements.selector());
        if (apk.debug()) {
            baseNameBuilder.append("-debug");
        }
        return baseNameBuilder.toString();
    }

    public static final class Requirements {
        private final int minSdkVersion;
        private final int maxSdkVersion;
        private final Collection<String> supportsScreens;
        private final Collection<String> compatibleScreens;
        private final Collection<String> supportsGlTextures;
        private final Collection<String> abis;
        private final Collection<String> usesFeatures;
        private final Collection<String> usesConfigurations;
        private final Collection<String> usesLibraries;

        private Requirements(
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

        @Override
        public int hashCode() {
            int h = 0;

            h ^= 0xffff0000 & minSdkVersion << 16;
            h ^= 0x0000ffff & maxSdkVersion;

            for (String supportsGlTexture : supportsGlTextures) {
                h ^= supportsGlTexture.hashCode();
            }
            for (String supportsScreen : supportsScreens) {
                h ^= supportsScreen.hashCode();
            }
            for (String compatibleScreen : compatibleScreens) {
                h ^= compatibleScreen.hashCode();
            }
            for (String usesFeature : usesFeatures) {
                h ^= usesFeature.hashCode();
            }
            for (String abi : abis) {
                h ^= abi.hashCode();
            }
            for (String usesLibrary : usesLibraries) {
                h ^= usesLibrary.hashCode();
            }
            for (String usesConfiguration : usesConfigurations) {
                h ^= usesConfiguration.hashCode();
            }

            return h;
        }

        public String selector() {
            final int hashCode = hashCode();
            final String hexCode = ChecksumUtils.intToHexString(hashCode);
            return hexCode.substring(0, 7);
        }
    }

    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor {
        private String applicationId;
        private int versionCode = 0;
        private String versionName;
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
            applicationId = dist.apk.applicationId();
            versionCode = dist.apk.versionCode();
            versionName = dist.apk.versionName();
            timestamp = dist.apk.timestamp();
            fingerprint = dist.apk.fingerprint();
            signatures = dist.apk.signatures();
            debug = dist.apk.debug();

            minSdkVersion = dist.requirements.minSdkVersion;
            maxSdkVersion = dist.requirements.maxSdkVersion;
            supportsScreens = dist.requirements.supportsScreens;
            compatibleScreens = dist.requirements.compatibleScreens;
            supportsGlTextures = dist.requirements.supportsGlTextures;
            abis = dist.requirements.abis;
            usesFeatures = dist.requirements.usesFeatures;
            usesConfigurations = dist.requirements.usesConfigurations;
            usesLibraries = dist.requirements.usesLibraries;

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

        public Editor versionName(String versionName) {
            this.versionName = versionName;
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
                    new Apk(applicationId,
                            versionCode,
                            versionName,
                            timestamp,
                            fingerprint,
                            signatures,
                            debug),
                    new Requirements(
                            minSdkVersion, maxSdkVersion,
                            supportsScreens,
                            compatibleScreens,
                            supportsGlTextures,
                            abis,
                            usesFeatures,
                            usesLibraries,
                            usesConfigurations),
                    meta,
                    internalMeta,
                    externalMeta);
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
