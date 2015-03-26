package io.github.eterverda.playless.common;

public class JsonConstants {
    // root elements
    public static final String APPLICATION_ID = "applicationId";

    // filter elements
    public static final String FILTER = "filter";
    public static final String MIN_SDK_VERSION = "minSdkVersion";
    public static final String MAX_SDK_VERSION = "maxSdkVersion";
    public static final String REQUIRES_SMALLEST_WIDTH_DP = "requiresSmallestWidthDp";
    public static final String USES_GL_ES = "usesGlEs";
    public static final String SUPPORTS_SCREENS = "supportsScreens";
    public static final String COMPATIBLE_SCREENS = "compatibleScreens";
    public static final String SUPPORTS_GL_TEXTURES = "supportsGlTextures";
    public static final String USES_FEATURES = "usesFeatures";
    public static final String USES_CONFIGURATIONS = "usesConfigurations";
    public static final String USES_LIBRARIES = "usesLibraries";
    public static final String NATIVE_CODE = "nativeCode";

    // version elements
    public static final String VERSION = "version";
    public static final String VERSION_CODE = "versionCode";
    public static final String TIMESTAMP = "timestamp";
    public static final String SIGNATURES = "signatures";
    public static final String FINGERPRINT = "fingerprint";
    public static final String DEBUG = "debug";

    public static final String SIGNATURES_PREFIX = SIGNATURES + "-";
    public static final String FINGERPRINT_PREFIX = FINGERPRINT + "-";

    // meta elements
    public static final String META = "meta";
}
