package io.github.eterverda.playless;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import io.github.eterverda.playless.common.Dist;

@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
public class DistMatcher {
    private final Context context;

    private transient String compatibleScreen;
    private transient Collection<String> features;
    private Collection<String> nativeCode;

    public DistMatcher(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * WARNING!
     *
     * This method does not check for GL Textures compatibility
     */
    public boolean matches(Dist.Filter filter) {
        if (filter.minSdkVersion > Build.VERSION.SDK_INT) {
            return false;
        }
        if (Build.VERSION.SDK_INT > filter.maxSdkVersion) {
            return false;
        }
        if (filter.requiresSmallestWidthDp > getSmallestScreenWidthDp()) {
            return false;
        }
        if (!filter.supportsScreens.contains(getSupportedScreen())) {
            return false;
        }
        if (!filter.compatibleScreens.isEmpty() && !filter.compatibleScreens.contains(getCompatibleScreen())) {
            return false;
        }
        if (!filter.usesFeatures.isEmpty() && getFeatures().containsAll(filter.usesFeatures)) {
            return false;
        }
        if (!filter.usesConfigurations.isEmpty() && !getConfigs().containsAll(filter.usesConfigurations)) {
            return false;
        }
        if (!filter.usesLibraries.isEmpty() && !getLibraries().containsAll(filter.usesLibraries)) {
            return false;
        }
        if (!filter.nativeCode.isEmpty() && !containsAny(filter.nativeCode, getNativeCode())) {
            return false;
        }

        return true;
    }

    @SuppressWarnings("deprecation")
    public Collection<String> getNativeCode() {
        if (nativeCode == null) {
            nativeCode = new ArrayList<>();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Collections.addAll(nativeCode, Build.SUPPORTED_ABIS);

            } else {
                nativeCode.add(Build.CPU_ABI);
                if (Build.CPU_ABI2 != null) {
                    nativeCode.add(Build.CPU_ABI2);
                }
            }
        }
        return nativeCode;
    }

    private Collection<String> getLibraries() {
        final ArrayList<String> libraries = new ArrayList<>();
        Collections.addAll(libraries, context.getPackageManager().getSystemSharedLibraryNames());
        return libraries;
    }

    private Collection<String> getConfigs() {
        // TODO implement
        throw new UnsupportedOperationException();
    }

    private Collection<String> getFeatures() {
        if (features == null) {
            features = new HashSet<>();
            for (FeatureInfo feature : context.getPackageManager().getSystemAvailableFeatures()) {
                if (feature.name == null) {
                    continue;
                }
                features.add(feature.name);
            }
        }
        return features;
    }

    private String getCompatibleScreen() {
        if (compatibleScreen == null) {
            compatibleScreen = getSupportedScreen() + "/" + getScreenDensityDpi();
        }
        return compatibleScreen;
    }

    private int getScreenDensityDpi() {
        return getDisplayMetrics().densityDpi;
    }

    private String getSupportedScreen() {
        switch (getScreenLayout() & Configuration.SCREENLAYOUT_SIZE_LARGE) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                return "small";
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                return "normal";
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                return "large";
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                return "xlarge";
            default:
                throw new AssertionError("Unsupported screen layout");
        }
    }

    private int getScreenLayout() {
        return getConfiguration().screenLayout;
    }

    private int getSmallestScreenWidthDp() {
        return getConfiguration().smallestScreenWidthDp;
    }

    private DisplayMetrics getDisplayMetrics() {
        return context.getResources().getDisplayMetrics();
    }

    private Configuration getConfiguration() {
        return context.getResources().getConfiguration();
    }

    private static boolean containsAny(Collection<String> requires, Collection<String> provides) {
        for (String req : requires) {
            if (provides.contains(req)) {
                return true;
            }
        }
        return false;
    }
}
