package io.github.eterverda.playless.core;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.eterverda.playless.common.Distribution;

public class AaptDistributionExtractor {
    private final CommandLine aapt;

    public AaptDistributionExtractor(String aapt) {
        this.aapt = new CommandLine(aapt)
                .addArgument("dump")
                .addArgument("badging");
    }

    public void extract(Distribution.Builder dist, File file) throws IOException {
        final CommandLine line = new CommandLine(aapt).addArgument(file.getAbsolutePath());

        final DistributionHandler handler = new DistributionHandler(dist);

        final DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        executor.execute(line);
    }

    private static class DistributionHandler implements ExecuteStreamHandler {
        private static final Pattern VERSION_CODE = Pattern.compile("package:.* versionCode='([0-9]*)'.*'");
        private static final Pattern VERSION_NAME = Pattern.compile("package:.* versionName='([^']*)'.*'");
        private static final Pattern APPLICATION_ID = Pattern.compile("package:.* name='([\\p{Alnum}\\.]*).*'");
        private static final Pattern MIN_SDK_VERSION = Pattern.compile("sdkVersion:'([0-9]*)'");
        private static final Pattern MAX_SDK_VERSION = Pattern.compile("maxSdkVersion:'([0-9]*)'");
        private static final Pattern DEBUGGABLE = Pattern.compile("application-debuggable");
        private static final Pattern USES_FEATURE = Pattern.compile("\\p{Space}*uses-feature:.* name='([\\p{Alnum}\\.]*)'");
        private static final Pattern USES_CONFIGURATION_TOUCH_SCREEN = Pattern.compile("uses-configuration:.*reqTouchScreen='([0-9]+)'.*");
        private static final Pattern USES_CONFIGURATION_KEYBOARD_TYPE = Pattern.compile("uses-configuration:.*reqKeyboardType='(-?[0-9]+)'.*");
        private static final Pattern USES_CONFIGURATION_NAVIGATION = Pattern.compile("uses-configuration:.*reqNavigation='(-?[0-9]+)'.*");
        private static final Pattern USES_CONFIGURATION_HARD_KEYBOARD = Pattern.compile("uses-configuration:.*reqHardKeyboard='-1'.*");
        private static final Pattern USES_CONFIGURATION_FIVE_WAY_NAV = Pattern.compile("uses-configuration:.*reqFiveWayNav='-1'.*");
        private static final Pattern LABEL = Pattern.compile("application-(label(?:-\\p{Alnum}+)*):'(.*)'");
        private static final Pattern ICON = Pattern.compile("application-(icon(?:-\\p{Alnum}+)*):'(.*)'");
        private static final Pattern SUPPORTS_SCREENS = Pattern.compile("supports-screens:(.*)");
        private static final Pattern REQUIRES_SMALLEST_WIDTH = Pattern.compile("requires-smallest-width:'(\\p{Digit}*)'");
        private static final Pattern COMPATIBLE_SCREENS = Pattern.compile("compatible-screens:(.*)");
        private static final Pattern SUPPORTS_GL_TEXTURES = Pattern.compile("supports-gl-texture:'(.*)'");
        private static final Pattern USES_LIBRARY = Pattern.compile("uses-library:'(.*)'");
        private static final Pattern ABIS = Pattern.compile("native-code:(.*)");

        private BufferedReader in;
        private Distribution.Builder dist;

        public DistributionHandler(Distribution.Builder dist) {
            this.dist = dist;
        }

        @Override
        public void setProcessInputStream(OutputStream os) throws IOException {

        }

        @Override
        public void setProcessErrorStream(InputStream is) throws IOException {

        }

        @Override
        public void setProcessOutputStream(InputStream in) throws IOException {
            this.in = new BufferedReader(new InputStreamReader(in));
        }

        @Override
        public void start() throws IOException {
            String line;
            while ((line = in.readLine()) != null) {
                final Matcher applicationId = APPLICATION_ID.matcher(line);
                if (applicationId.matches()) {
                    dist.applicationId(applicationId.group(1));
                }
                final Matcher versionCode = VERSION_CODE.matcher(line);
                if (versionCode.matches()) {
                    dist.versionCode(Integer.parseInt(versionCode.group(1)));
                }
                final Matcher versionName = VERSION_NAME.matcher(line);
                if (versionName.matches()) {
                    dist.versionName(versionName.group(1));
                }
                final Matcher minSdkVersion = MIN_SDK_VERSION.matcher(line);
                if (minSdkVersion.matches()) {
                    dist.minSdkVersion(Integer.parseInt(minSdkVersion.group(1)));
                }
                final Matcher maxSdkVersion = MAX_SDK_VERSION.matcher(line);
                if (maxSdkVersion.matches()) {
                    dist.maxSdkVersion(Integer.parseInt(maxSdkVersion.group(1)));
                }
                final Matcher debuggable = DEBUGGABLE.matcher(line);
                if (debuggable.matches()) {
                    dist.debug(true);
                }
                final Matcher usesFeature = USES_FEATURE.matcher(line);
                if (usesFeature.matches()) {
                    dist.usesFeature(usesFeature.group(1));
                }
                final Matcher usesConfigurationTouchScreen = USES_CONFIGURATION_TOUCH_SCREEN.matcher(line);
                if (usesConfigurationTouchScreen.matches()) {
                    dist.usesConfiguration("touchScreen/" + usesConfigurationTouchScreen.group(1));
                }
                final Matcher usesConfigurationKeyboardType = USES_CONFIGURATION_KEYBOARD_TYPE.matcher(line);
                if (usesConfigurationKeyboardType.matches()) {
                    dist.usesConfiguration("keyboardType/" + usesConfigurationKeyboardType.group(1));
                }
                final Matcher usesConfigurationNavigation = USES_CONFIGURATION_NAVIGATION.matcher(line);
                if (usesConfigurationNavigation.matches()) {
                    dist.usesConfiguration("navigation/" + usesConfigurationNavigation.group(1));
                }
                final Matcher usesConfigurationHardKeyboard = USES_CONFIGURATION_HARD_KEYBOARD.matcher(line);
                if (usesConfigurationHardKeyboard.matches()) {
                    dist.usesConfiguration("hardKeyboard");
                }
                final Matcher usesConfigurationFiveWayNav = USES_CONFIGURATION_FIVE_WAY_NAV.matcher(line);
                if (usesConfigurationFiveWayNav.matches()) {
                    dist.usesConfiguration("fiveWayNav");
                }
                final Matcher label = LABEL.matcher(line);
                if (label.matches()) {
                    dist.meta(label.group(1), label.group(2));
                }
                final Matcher icon = ICON.matcher(line);
                if (icon.matches()) {
                    dist.internalMeta(icon.group(1), icon.group(2));
                }
                final Matcher supportsScreens = SUPPORTS_SCREENS.matcher(line);
                if (supportsScreens.matches()) {
                    dist.supportsScreen(split(supportsScreens.group(1)));
                }
                final Matcher requiresSmallestWidth = REQUIRES_SMALLEST_WIDTH.matcher(line);
                if (requiresSmallestWidth.matches()) {
                    dist.supportsScreen("sw" + requiresSmallestWidth.group(1) + "dp");
                }
                final Matcher compatibleScreens = COMPATIBLE_SCREENS.matcher(line);
                if (compatibleScreens.matches()) {
                    for (String compatibleScreen : split(compatibleScreens.group(1))) {
                        final String readableScreen = compatibleScreen
                                .replace("200", "small")
                                .replace("300", "normal")
                                .replace("400", "large")
                                .replace("500", "xlarge");
                        dist.compatibleScreen(readableScreen);
                    }
                }
                final Matcher supportsGlTexture = SUPPORTS_GL_TEXTURES.matcher(line);
                if (supportsGlTexture.matches()) {
                    dist.supportsGlTexture(supportsGlTexture.group(1));
                }
                final Matcher usesLibrary = USES_LIBRARY.matcher(line);
                if (usesLibrary.matches()) {
                    dist.usesLibrary(usesLibrary.group(1));
                }
                final Matcher abis = ABIS.matcher(line);
                if (abis.matches()) {
                    dist.abi(split(abis.group(1)));
                }
            }
        }

        @Override
        public void stop() throws IOException {
        }

        public static String[] split(String string) {
            return string.replaceAll("\\p{Space}*'(.*)'\\p{Space}*", "$1").split("'\\p{Space}+'");
        }
    }
}
