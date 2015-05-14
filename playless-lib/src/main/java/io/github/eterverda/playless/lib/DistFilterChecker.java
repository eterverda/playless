package io.github.eterverda.playless.lib;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.FeatureInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.Dist.Filter;
import io.github.eterverda.playless.common.Dist.Filter.Config;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class DistFilterChecker {
    private final Context context;

    private transient String myCompatibleScreen;
    private transient String mySupportedScreen;
    private transient Collection<String> myFeatures;
    private transient Collection<String> myNativeCode;
    private transient ConfigurationInfo myConfigurationInfo;
    private transient Config myConfig;
    private transient Collection<String> myLibraries;

    public DistFilterChecker(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * WARNING!
     * <p/>
     * This method does not check for GL Textures compatibility and will return false if filter
     * contains any supportsGlTextures
     */
    public boolean isCompatible(Dist dist) {
        return isCompatible(dist.filter);
    }

    /**
     * WARNING!
     * <p/>
     * This method does not check for GL Textures compatibility and will return false if filter
     * contains any supportsGlTextures
     */
    public boolean isCompatible(Filter filter) {
        return minSdkVersion(filter) &&
                maxSdkVersion(filter) &&
                requiresSmallestWidthDp(filter) &&
                usesGlEs(filter) &&
                supportedScreens(filter) &&
                compatibleScreens(filter) &&
                supportsGlTextures(filter) &&
                usesFeatures(filter) &&
                usesLibraries(filter) &&
                usesConfigurations(filter) &&
                nativeCode(filter);
    }

    private boolean usesGlEs(Filter filter) {
        return filter.usesGlEs < myGlEs();
    }

    private boolean minSdkVersion(Filter filter) {
        return filter.minSdkVersion <= Build.VERSION.SDK_INT;
    }

    private boolean maxSdkVersion(Filter filter) {
        return Build.VERSION.SDK_INT <= filter.maxSdkVersion;
    }

    private boolean requiresSmallestWidthDp(Filter filter) {
        return filter.requiresSmallestWidthDp <= mySmallestScreenWidthDp();
    }

    private boolean supportedScreens(Filter filter) {
        return filter.supportsScreens.contains(mySupportedScreen());
    }

    private boolean compatibleScreens(Filter filter) {
        return filter.compatibleScreens.isEmpty() ||
                filter.compatibleScreens.contains(myCompatibleScreen());
    }

    private boolean supportsGlTextures(Filter filter) {
        // TODO implement
        return filter.supportsGlTextures.isEmpty();
    }

    private boolean usesFeatures(Filter filter) {
        return filter.usesFeatures.isEmpty() ||
                myFeatures().containsAll(filter.usesFeatures);
    }

    private boolean usesLibraries(Filter filter) {
        return filter.usesLibraries.isEmpty() || myLibraries().containsAll(filter.usesLibraries);
    }

    private boolean usesConfigurations(Filter filter) {
        return filter.usesConfigurations.isEmpty() || matchesAnyConfiguration(myConfiguration(), filter.usesConfigurations);
    }

    private boolean nativeCode(Filter filter) {
        return filter.nativeCode.isEmpty() ||
                containsAny(myNativeCode(), filter.nativeCode);
    }

    @SuppressWarnings("deprecation")
    private Collection<String> myNativeCode() {
        if (myNativeCode == null) {
            myNativeCode = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Collections.addAll(myNativeCode, Build.SUPPORTED_ABIS);

            } else {
                myNativeCode.add(Build.CPU_ABI);
                if (Build.CPU_ABI2 != null) {
                    myNativeCode.add(Build.CPU_ABI2);
                }
            }
        }
        return myNativeCode;
    }

    private Collection<String> myLibraries() {
        if (myLibraries == null) {
            myLibraries = new HashSet<>();
            Collections.addAll(myLibraries, context.getPackageManager().getSystemSharedLibraryNames());
        }
        return myLibraries;
    }

    private Collection<String> myFeatures() {
        if (myFeatures == null) {
            myFeatures = new HashSet<>();
            for (FeatureInfo feature : context.getPackageManager().getSystemAvailableFeatures()) {
                if (feature.name == null) {
                    continue;
                }
                myFeatures.add(feature.name);
            }
        }
        return myFeatures;
    }

    private Config myConfiguration() {
        if (myConfig == null) {
            final ConfigurationInfo configurationInfo = myConfigurationInfo();
            final int fiveWayNav = (configurationInfo.reqInputFeatures & ConfigurationInfo.INPUT_FEATURE_FIVE_WAY_NAV) == 0 ? 0 : -1;
            final int hardKeyboard = (configurationInfo.reqInputFeatures & ConfigurationInfo.INPUT_FEATURE_HARD_KEYBOARD) == 0 ? 0 : -1;

            myConfig = new Config(
                    fiveWayNav,
                    hardKeyboard,
                    configurationInfo.reqKeyboardType,
                    configurationInfo.reqNavigation,
                    configurationInfo.reqTouchScreen);
        }
        return myConfig;
    }

    private int myGlEs() {
        return myConfigurationInfo().reqGlEsVersion;
    }

    private ConfigurationInfo myConfigurationInfo() {
        if (myConfigurationInfo == null) {
            final ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            myConfigurationInfo = manager.getDeviceConfigurationInfo();
        }
        return myConfigurationInfo;
    }

    private String myCompatibleScreen() {
        if (myCompatibleScreen == null) {
            myCompatibleScreen = mySupportedScreen() + "/" + myScreenDensityDpi();
        }
        return myCompatibleScreen;
    }

    private int myScreenDensityDpi() {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    private String mySupportedScreen() {
        if (mySupportedScreen == null) {
            switch (context.getResources().getConfiguration().screenLayout & android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK) {
                case android.content.res.Configuration.SCREENLAYOUT_SIZE_SMALL:
                    mySupportedScreen = "small";
                    break;
                case android.content.res.Configuration.SCREENLAYOUT_SIZE_NORMAL:
                    mySupportedScreen = "normal";
                    break;
                case android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE:
                    mySupportedScreen = "large";
                    break;
                case android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE:
                    mySupportedScreen = "xlarge";
                    break;
                default:
                    throw new AssertionError("Unsupported screen layout");
            }
        }
        return mySupportedScreen;
    }

    private int mySmallestScreenWidthDp() {
        return context.getResources().getConfiguration().smallestScreenWidthDp;
    }

    private static boolean matchesAnyConfiguration(Config provides, Collection<Config> requires) {
        for (Config config : requires) {
            if (matchesConfiguration(provides, config)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesConfiguration(Config provides, Config requires) {
        return matchesConfigurationField(requires.fiveWayNav, provides.fiveWayNav) &&
                matchesConfigurationField(requires.hardKeyboard, provides.hardKeyboard) &&
                matchesConfigurationField(requires.keyboardType, provides.keyboardType) &&
                matchesConfigurationField(requires.navigation, provides.navigation) &&
                matchesConfigurationField(requires.touchScreen, provides.touchScreen);
    }

    private static boolean matchesConfigurationField(int requires, int provides) {
        return requires == 0 || requires == provides;
    }

    private static boolean containsAny(Collection<String> provides, Collection<String> requires) {
        for (String req : requires) {
            if (provides.contains(req)) {
                return true;
            }
        }
        return false;
    }
}
