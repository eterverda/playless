package io.github.eterverda.playless.common;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import io.github.eterverda.playless.common.util.ObjectEquals;
import io.github.eterverda.util.checksum.Checksum;

import static io.github.eterverda.playless.common.Repo.Editor.*;

@Immutable
@ThreadSafe
public final class Dist {
    public static final String LINK_REL_DOWNLOAD = "download";
    public static final String LINK_REL_STORE = "store";
    public static final String LINK_REL_ICON = "icon";

    public static final String META_VERSION_NAME = "versionName";
    public static final String META_LABEL = "label";
    public static final String META_DOWNLOAD_SIZE = "downloadSize";

    @NotNull
    public final String applicationId;
    @NotNull
    public final Version version;
    @NotNull
    public final Filter filter;
    @NotNull
    public final Set<Link> links;
    @NotNull
    public final Map<String, String> meta;

    protected Dist(
            @NotNull String applicationId,
            @NotNull Version version,
            @NotNull Filter filter,
            @NotNull Set<Link> links,
            @NotNull Map<String, String> meta) {

        this.applicationId = applicationId;
        this.version = version;
        this.filter = filter;
        this.links = links;
        this.meta = meta;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Dist && equals((Dist) other);
    }

    public boolean equals(Dist other) {
        return equalsIgnoreMeta(other) && meta.equals(other.meta);
    }

    public boolean equalsIgnoreMeta(Dist other) {
        return this == other || other != null &&
                applicationId.equals(other.applicationId) &&
                version.equals(other.version) &&
                filter.equals(other.filter);
    }

    @Override
    public int hashCode() {
        int result = applicationId.hashCode();
        result = 31 * result + version.hashCode();
        result = 31 * result + filter.hashCode();
        result = 31 * result + meta.hashCode();
        return result;
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

        protected Version(
                int versionCode, long timestamp,
                @Nullable Checksum fingerprint, @Nullable Checksum signatures,
                boolean debug) {

            this.versionCode = versionCode;
            this.timestamp = timestamp;
            this.fingerprint = fingerprint;
            this.signatures = signatures;
            this.debug = debug;
        }

        public boolean equals(Object other) {
            return other instanceof Version && equals((Version) other);
        }

        public boolean equals(Version other) {
            return equalsIgnoreFingerprint(other) &&
                    ObjectEquals.equals(fingerprint, other.fingerprint);
        }

        public boolean equalsIgnoreFingerprint(Version other) {
            return playfulEquals(other) &&
                    ObjectEquals.equals(signatures, other.signatures) &&
                    debug == other.debug;
        }

        public boolean playfulEquals(Version other) {
            return this == other || other != null &&
                    versionCode == other.versionCode;
        }

        @Override
        public int hashCode() {
            int h = versionCode;
            h = 31 * h + (fingerprint != null ? fingerprint.hashCode() : 0);
            h = 31 * h + (signatures != null ? signatures.hashCode() : 0);
            return debug ? h : ~h;
        }
    }

    @Immutable
    @ThreadSafe
    public static final class Filter {
        public final int minSdkVersion;
        public final int maxSdkVersion;
        public final int requiresSmallestWidthDp;
        public final int usesGlEs;
        public final Set<String> supportsScreens;
        public final Set<String> compatibleScreens;
        public final Set<String> supportsGlTextures;
        public final Set<String> usesFeatures;
        public final Set<Config> usesConfigurations;
        public final Set<String> usesLibraries;
        public final Set<String> nativeCode;

        protected Filter(
                int minSdkVersion, int maxSdkVersion,
                int requiresSmallestWidthDp,
                int usesGlEs,
                @NotNull Set<String> supportsScreens,
                @NotNull Set<String> compatibleScreens,
                @NotNull Set<String> supportsGlTextures,
                @NotNull Set<String> usesFeatures,
                @NotNull Set<String> usesLibraries,
                @NotNull Set<Config> usesConfigurations,
                @NotNull Set<String> nativeCode) {

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
        public boolean equals(Object other) {
            return other instanceof Filter && equals((Filter) other);
        }

        public boolean equals(Filter other) {
            return playfulEquals(other) &&
                    usesGlEs == other.usesGlEs &&
                    usesConfigurations.equals(other.usesConfigurations) &&
                    usesLibraries.equals(other.usesLibraries);
        }

        public boolean playfulEquals(Filter other) {
            return this == other || other != null &&
                    minSdkVersion == other.minSdkVersion &&
                    maxSdkVersion == other.maxSdkVersion &&
                    requiresSmallestWidthDp == other.requiresSmallestWidthDp &&
                    supportsScreens.equals(other.supportsScreens) &&
                    compatibleScreens.equals(other.compatibleScreens) &&
                    supportsGlTextures.equals(other.supportsGlTextures) &&
                    usesFeatures.equals(other.usesFeatures) &&
                    nativeCode.equals(other.nativeCode);
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
                return this == other || other != null && equals(
                        other.fiveWayNav,
                        other.hardKeyboard,
                        other.keyboardType,
                        other.navigation,
                        other.touchScreen);
            }

            public boolean equals(
                    int fiveWayNav,
                    int hardKeyboard,
                    int keyboardType,
                    int navigation,
                    int touchScreen) {

                return this.fiveWayNav == fiveWayNav &&
                        this.hardKeyboard == hardKeyboard &&
                        this.keyboardType == keyboardType &&
                        this.navigation == navigation &&
                        this.touchScreen == touchScreen;
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
        private Set<String> supportsScreens;
        private Set<String> compatibleScreens;
        private Set<String> supportsGlTextures;
        private Set<String> usesFeatures;
        private Set<Filter.Config> usesConfigurations;
        private Set<String> usesLibraries;
        private Set<String> nativeCodes;

        private Set<Link> links;
        private Map<String, String> meta;

        public Editor() {
            supportsScreens = Collections.emptySet();
            compatibleScreens = Collections.emptySet();
            supportsGlTextures = Collections.emptySet();
            usesFeatures = Collections.emptySet();
            usesConfigurations = Collections.emptySet();
            usesLibraries = Collections.emptySet();
            nativeCodes = Collections.emptySet();

            links = Collections.emptySet();
            meta = Collections.emptyMap();
        }

        protected Editor(Dist dist) {
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
            nativeCodes = dist.filter.nativeCode;

            links = dist.links;
            meta = dist.meta;
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

        public void supportsScreen(String supportsScreen) {
            supportsScreens = modifiableTreeSet(supportsScreens);
            supportsScreens.add(supportsScreen);
        }

        public void compatibleScreen(String compatibleScreen) {
            compatibleScreens = modifiableTreeSet(compatibleScreens);
            compatibleScreens.add(compatibleScreen);
        }

        public void supportsGlTexture(String supportsGlTexture) {
            supportsGlTextures = modifiableTreeSet(supportsGlTextures);
            supportsGlTextures.add(supportsGlTexture);
        }

        public void usesFeature(String usesFeature) {
            usesFeatures = modifiableTreeSet(usesFeatures);
            usesFeatures.add(usesFeature);
        }

        public void usesConfiguration(int fiveWayNav, int hardKeyboard, int keyboardType, int navigation, int touchScreen) {
            usesConfiguration(new Filter.Config(fiveWayNav, hardKeyboard, keyboardType, navigation, touchScreen));
        }

        public void usesConfiguration(Filter.Config usesConfiguration) {
            usesConfigurations = modifiableHashSet(usesConfigurations);
            usesConfigurations.add(usesConfiguration);
        }

        public void usesLibrary(String usesLibrary) {
            usesLibraries = modifiableTreeSet(usesLibraries);
            usesLibraries.add(usesLibrary);
        }

        public void nativeCode(String nativeCode) {
            nativeCodes = modifiableTreeSet(nativeCodes);
            nativeCodes.add(nativeCode);
        }

        public void link(Link link) {
            links = modifiableTreeSet(links);
            links.add(link);
        }

        public void link(String rel, String href) {
            link(new Link(rel, href));
        }

        public void unlink(Link link) {
            links = modifiableTreeSet(links);
            links.remove(link);
        }

        public void meta(String key, String value) {
            meta = modifiableTreeMap(meta);
            meta.put(key, value);
        }

        public void unmeta(String key) {
            meta = modifiableTreeMap(meta);
            meta.remove(key);
        }

        @NotNull
        public Dist build() {
            meta = unmodifiableMap(meta);
            links = unmodifiableSet(links);

            return new Dist(
                    applicationId,
                    buildVersion(),
                    buildFilter(),
                    links,
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
            supportsScreens = unmodifiableSet(supportsScreens);
            compatibleScreens = unmodifiableSet(compatibleScreens);
            supportsGlTextures = unmodifiableSet(supportsGlTextures);
            usesFeatures = unmodifiableSet(usesFeatures);
            usesLibraries = unmodifiableSet(usesLibraries);
            usesConfigurations = unmodifiableSet(usesConfigurations);
            nativeCodes = unmodifiableSet(nativeCodes);

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
                    nativeCodes);
        }
    }
}
