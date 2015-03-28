package io.github.eterverda.playless.common;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import io.github.eterverda.util.checksum.Checksum;

@Immutable
@ThreadSafe
public final class Dist {
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

    private Dist(
            @NotNull String applicationId,
            @NotNull Version version,
            @NotNull Filter filter,
            @NotNull Map<String, String> meta) {

        this.applicationId = applicationId;
        this.version = version;
        this.filter = filter;
        this.meta = meta;
    }

    @Immutable
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

    @Immutable
    @ThreadSafe
    public static final class Filter {
        public final int minSdkVersion;
        public final int maxSdkVersion;
        public final int requiresSmallestWidthDp;
        public final int usesGlEs;
        public final Collection<String> supportsScreens;
        public final Collection<String> compatibleScreens;
        public final Collection<String> supportsGlTextures;
        public final Collection<String> usesFeatures;
        public final Collection<Config> usesConfigurations;
        public final Collection<String> usesLibraries;
        public final Collection<String> nativeCode;

        private Filter(
                int minSdkVersion, int maxSdkVersion,
                int requiresSmallestWidthDp,
                int usesGlEs,
                @NotNull Collection<String> supportsScreens,
                @NotNull Collection<String> compatibleScreens,
                @NotNull Collection<String> supportsGlTextures,
                @NotNull Collection<String> usesFeatures,
                @NotNull Collection<String> usesLibraries,
                @NotNull Collection<Config> usesConfigurations,
                @NotNull Collection<String> nativeCode) {

            this.minSdkVersion = minSdkVersion;
            this.maxSdkVersion = maxSdkVersion;
            this.requiresSmallestWidthDp = requiresSmallestWidthDp;
            this.usesGlEs = usesGlEs;
            this.supportsScreens = supportsScreens;
            this.compatibleScreens = compatibleScreens;
            this.supportsGlTextures = supportsGlTextures;
            this.usesFeatures = usesFeatures;
            this.usesLibraries = usesLibraries;
            this.usesConfigurations = usesConfigurations;
            this.nativeCode = nativeCode;
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
            h ^= hashCode(usesLibraries);
            h ^= hashCode(usesConfigurations);
            h ^= hashCode(nativeCode);

            return h;
        }

        private int hashCode(Collection<?> collection) {
            int h = 0;
            for (Object e : collection) {
                h ^= e.hashCode();
            }
            return h;
        }

        public static final class Config {
            public final int fiveWayNav;
            public final int hardKeyboard;
            public final int keyboardType;
            public final int navigation;
            public final int touchScreen;

            public Config(
                    int fiveWayNav,
                    int hardKeyboard,
                    int keyboardType,
                    int navigation,
                    int touchScreen) {

                this.fiveWayNav = fiveWayNav;
                this.hardKeyboard = hardKeyboard;
                this.keyboardType = keyboardType;
                this.navigation = navigation;
                this.touchScreen = touchScreen;
            }

            @Override
            public boolean equals(Object other) {
                return other instanceof Config && equals((Config) other);
            }

            public boolean equals(Config other) {
                return this == other || other != null &&
                        fiveWayNav == other.fiveWayNav &&
                        hardKeyboard == other.hardKeyboard &&
                        keyboardType == other.keyboardType &&
                        navigation == other.navigation &&
                        touchScreen == other.touchScreen;
            }

            @Override
            public int hashCode() {
                int result = 0;
                result |= fiveWayNav << 16;
                result |= hardKeyboard << 12;
                result |= keyboardType << 8;
                result |= navigation << 4;
                result |= touchScreen;
                return result * 31;
            }
        }
    }

    public Editor edit() {
        return new Editor(this);
    }

    @NotThreadSafe
    public static final class Editor {
        private String applicationId;

        private int versionCode = 0;
        private long timestamp = Long.MIN_VALUE;
        private Checksum fingerprint;
        private Checksum signatures;
        private boolean debug;

        private int minSdkVersion = 1;
        private int maxSdkVersion = Integer.MAX_VALUE;
        private int requiresSmallestWidthDp;
        private int usesGlEs;
        private Collection<String> supportsScreens;
        private Collection<String> compatibleScreens;
        private Collection<String> supportsGlTextures;
        private Collection<String> usesFeatures;
        private Collection<Filter.Config> usesConfigurations;
        private Collection<String> usesLibraries;
        private Collection<String> nativeCode;

        private Map<String, String> meta;

        private boolean shared;

        public Editor() {
            supportsScreens = new TreeSet<>();
            compatibleScreens = new TreeSet<>();
            supportsGlTextures = new TreeSet<>();
            usesFeatures = new TreeSet<>();
            usesConfigurations = new HashSet<>();
            usesLibraries = new TreeSet<>();
            nativeCode = new TreeSet<>();

            meta = new TreeMap<>();
        }

        private Editor(Dist dist) {
            applicationId = dist.applicationId;

            versionCode = dist.version.versionCode;
            timestamp = dist.version.timestamp;
            fingerprint = dist.version.fingerprint;
            signatures = dist.version.signatures;
            debug = dist.version.debug;

            minSdkVersion = dist.filter.minSdkVersion;
            maxSdkVersion = dist.filter.maxSdkVersion;
            requiresSmallestWidthDp = dist.filter.requiresSmallestWidthDp;
            usesGlEs = dist.filter.usesGlEs;
            supportsScreens = dist.filter.supportsScreens;
            compatibleScreens = dist.filter.compatibleScreens;
            supportsGlTextures = dist.filter.supportsGlTextures;
            usesFeatures = dist.filter.usesFeatures;
            usesConfigurations = dist.filter.usesConfigurations;
            usesLibraries = dist.filter.usesLibraries;
            nativeCode = dist.filter.nativeCode;

            meta = dist.meta;

            shared = true;
        }

        public void applicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public void versionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public void timestamp(long timestamp) {
            this.timestamp = timestamp;
        }

        public void fingerprint(Checksum fingerprint) {
            this.fingerprint = fingerprint;
        }

        public void signatures(Checksum signatures) {
            this.signatures = signatures;
        }

        public void debug(boolean debug) {
            this.debug = debug;
        }

        public void minSdkVersion(int minSdkVersion) {
            this.minSdkVersion = minSdkVersion;
        }

        public void maxSdkVersion(int maxSdkVersion) {
            this.maxSdkVersion = maxSdkVersion;
        }

        public void requiresSmallestWidthDp(int requiresSmallestWidthDp) {
            this.requiresSmallestWidthDp = requiresSmallestWidthDp;
        }

        public void usesGlEs(int usesGlEs) {
            this.usesGlEs = usesGlEs;
        }

        public void supportsScreen(String... supportsScreens) {
            unShare();
            Collections.addAll(this.supportsScreens, supportsScreens);
        }

        public void compatibleScreen(String... compatibleScreens) {
            unShare();
            Collections.addAll(this.compatibleScreens, compatibleScreens);
        }

        public void supportsGlTexture(String... supportsGlTextures) {
            unShare();
            Collections.addAll(this.supportsGlTextures, supportsGlTextures);
        }

        public void usesFeature(String... usesFeatures) {
            unShare();
            Collections.addAll(this.usesFeatures, usesFeatures);
        }

        public void usesConfiguration(int fiveWayNav, int hardKeyboard, int keyboardType, int navigation, int touchScreen) {
            usesConfiguration(new Filter.Config(fiveWayNav, hardKeyboard, keyboardType, navigation, touchScreen));
        }

        public void usesConfiguration(Filter.Config... usesConfigurations) {
            unShare();
            Collections.addAll(this.usesConfigurations, usesConfigurations);
        }

        public void usesLibrary(String... usesLibraries) {
            unShare();
            Collections.addAll(this.usesLibraries, usesLibraries);
        }

        public void nativeCode(String... nativeCode) {
            unShare();
            Collections.addAll(this.nativeCode, nativeCode);
        }

        public void meta(String key, String value) {
            unShare();
            meta.put(key, value);
        }

        @NotNull
        public Dist build() {
            share();

            return new Dist(
                    applicationId,
                    buildVersion(),
                    buildFilter(),
                    meta);
        }

        @NotNull
        public Version buildVersion() {
            return new Version(versionCode,
                    timestamp,
                    fingerprint,
                    signatures,
                    debug);
        }

        @NotNull
        private Filter buildFilter() {
            share();

            return new Filter(
                    minSdkVersion, maxSdkVersion,
                    requiresSmallestWidthDp,
                    usesGlEs,
                    supportsScreens,
                    compatibleScreens,
                    supportsGlTextures,
                    usesFeatures,
                    usesLibraries,
                    usesConfigurations,
                    nativeCode);
        }

        private void share() {
            if (shared) {
                return;
            }
            supportsScreens = unmodifiableCollection(supportsScreens);
            compatibleScreens = unmodifiableCollection(compatibleScreens);
            supportsGlTextures = unmodifiableCollection(supportsGlTextures);
            usesFeatures = unmodifiableCollection(usesFeatures);
            usesLibraries = unmodifiableCollection(usesLibraries);
            usesConfigurations = unmodifiableCollection(usesConfigurations);
            nativeCode = unmodifiableCollection(nativeCode);

            meta = unmodifiableMap(meta);

            shared = true;
        }

        private void unShare() {
            if (!shared) {
                return;
            }
            supportsScreens = new TreeSet<>(supportsScreens);
            compatibleScreens = new TreeSet<>(compatibleScreens);
            supportsGlTextures = new TreeSet<>(supportsGlTextures);
            usesFeatures = new TreeSet<>(usesFeatures);
            usesConfigurations = new HashSet<>(usesConfigurations);
            usesLibraries = new TreeSet<>(usesLibraries);
            nativeCode = new TreeSet<>(nativeCode);

            meta = new TreeMap<>(meta);

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
