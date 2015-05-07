package io.github.eterverda.playless.core;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.util.DistFactories;
import io.github.eterverda.playless.core.util.jar.Jars;

public class InitialDistFactory {
    @NotNull
    private final File aapt;

    public InitialDistFactory(@NotNull File aapt) {
        this.aapt = aapt;
    }

    @NotNull
    public Dist load(@NotNull File file) throws IOException {
        final Dist.Editor dist = new Dist.Editor();

        loadFileTo(dist, file);
        loadAaptTo(dist, file);

        return dist.build();
    }

    private static void loadFileTo(@NotNull Dist.Editor dist, @NotNull File file) throws IOException {
        loadTimestampTo(dist, file);

        dist.meta(Dist.META_DOWNLOAD_SIZE, Long.toString(file.length()));
        dist.link(Dist.LINK_REL_DOWNLOAD, "file:" + file.getAbsolutePath());
        dist.fingerprint(DistFactories.loadFingerprint(file));
        dist.signatures(Jars.loadSignatures(file));
    }

    private static void loadTimestampTo(@NotNull Dist.Editor dist, @NotNull File file) {
        dist.timestamp(file.lastModified());
    }

    private void loadAaptTo(@NotNull Dist.Editor dist, @NotNull File file) throws IOException {
        final CommandLine line = new CommandLine(aapt)
                .addArgument("dump")
                .addArgument("badging")
                .addArgument(file.getAbsolutePath());

        final AaptStreamHandler handler = new AaptStreamHandler(file, dist);

        final DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        executor.execute(line);
    }

    private static class AaptStreamHandler implements ExecuteStreamHandler {
        private static final Pattern VERSION_CODE = Pattern.compile("package:.* versionCode='([0-9]*)'.*'");
        private static final Pattern VERSION_NAME = Pattern.compile("package:.* versionName='([^']*)'.*'");
        private static final Pattern APPLICATION_ID = Pattern.compile("package:.* name='([\\p{Alnum}\\.]*).*'");
        private static final Pattern MIN_SDK_VERSION = Pattern.compile("sdkVersion:'([0-9]*)'");
        private static final Pattern MAX_SDK_VERSION = Pattern.compile("maxSdkVersion:'([0-9]*)'");
        private static final Pattern DEBUGGABLE = Pattern.compile("application-debuggable");
        private static final Pattern USES_FEATURE = Pattern.compile("\\p{Space}*uses-feature:.* name='([\\p{Alnum}\\.]*)'");
        private static final Pattern USES_GL_ES = Pattern.compile("\\p{Space}*uses-gl-es: '0x([0-9a-fA-F]*)'");
        private static final Pattern USES_CONFIGURATION = Pattern.compile("uses-configuration:(.*)");
        private static final Pattern CONFIG_FIVE_WAY_NAV = Pattern.compile(".*reqFiveWayNav='(-?[0-9]+)'.*");
        private static final Pattern CONFIG_HARD_KEYBOARD = Pattern.compile(".*reqHardKeyboard='(-?[0-9]+)'.*");
        private static final Pattern CONFIG_TOUCH_SCREEN = Pattern.compile(".*reqTouchScreen='([0-9]+)'.*");
        private static final Pattern CONFIG_KEYBOARD_TYPE = Pattern.compile(".*reqKeyboardType='(-?[0-9]+)'.*");
        private static final Pattern CONFIG_NAVIGATION = Pattern.compile(".*reqNavigation='(-?[0-9]+)'.*");
        private static final Pattern LABEL = Pattern.compile("application-label((?:-\\p{Alnum}+)*):'(.*)'");
        private static final Pattern ICON = Pattern.compile("application-icon((?:-\\p{Alnum}+)*):'(.*)'");
        private static final Pattern SUPPORTS_SCREENS = Pattern.compile("supports-screens:(.*)");
        private static final Pattern REQUIRES_SMALLEST_WIDTH = Pattern.compile("requires-smallest-width:'(\\p{Digit}*)'");
        private static final Pattern COMPATIBLE_SCREENS = Pattern.compile("compatible-screens:(.*)");
        private static final Pattern SUPPORTS_GL_TEXTURES = Pattern.compile("supports-gl-texture:'(.*)'");
        private static final Pattern USES_LIBRARY = Pattern.compile("uses-library:'(.*)'");
        private static final Pattern NATIVE_CODE = Pattern.compile("native-code:(.*)");

        private BufferedReader in;
        private File file;
        private Dist.Editor dist;

        public AaptStreamHandler(File file, Dist.Editor dist) {
            this.file = file;
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
            final HashMap<String, String> labels = new HashMap<>();

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
                    dist.meta(Dist.META_VERSION_NAME, versionName.group(1));
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
                final Matcher usesGlEs = USES_GL_ES.matcher(line);
                if (usesGlEs.matches()) {
                    dist.usesGlEs(Integer.parseInt(usesGlEs.group(1), 0x10));
                }
                final Matcher usesConfiguration = USES_CONFIGURATION.matcher(line);
                if (usesConfiguration.matches()) {
                    final String string = usesConfiguration.group(1);

                    final int fiveWayNav = extract(string, CONFIG_FIVE_WAY_NAV);
                    final int hardKeyboard = extract(string, CONFIG_HARD_KEYBOARD);
                    final int touchScreen = extract(string, CONFIG_TOUCH_SCREEN);
                    final int keyboardType = extract(string, CONFIG_KEYBOARD_TYPE);
                    final int navigation = extract(string, CONFIG_NAVIGATION);

                    dist.usesConfiguration(fiveWayNav, hardKeyboard, keyboardType, navigation, touchScreen);
                }
                final Matcher label = LABEL.matcher(line);
                if (label.matches()) {
                    labels.put(Dist.META_LABEL + label.group(1), label.group(2));
                }
                final Matcher icon = ICON.matcher(line);
                if (icon.matches()) {
                    if (!icon.group(1).contains("-65535")) { // skip scalable icons while they're unsupported
                        dist.link(Dist.LINK_REL_ICON + icon.group(1), "zip:file:" + file.getAbsolutePath() + "!/" + icon.group(2));
                    }
                }
                final Matcher supportsScreens = SUPPORTS_SCREENS.matcher(line);
                if (supportsScreens.matches()) {
                    for (String supportsScreen : split(supportsScreens.group(1))) {
                        dist.supportsScreen(supportsScreen);
                    }
                }
                final Matcher requiresSmallestWidth = REQUIRES_SMALLEST_WIDTH.matcher(line);
                if (requiresSmallestWidth.matches()) {
                    dist.requiresSmallestWidthDp(Integer.parseInt(requiresSmallestWidth.group(1)));
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
                final Matcher nativeCode = NATIVE_CODE.matcher(line);
                if (nativeCode.matches()) {
                    for (String code : split(nativeCode.group(1))) {
                        dist.nativeCode(code);
                    }
                }
            }

            final String defLabelValue = labels.get(Dist.META_LABEL);
            for (Map.Entry<String, String> l : labels.entrySet()) {
                final String key = l.getKey();
                final String value = l.getValue();
                if (defLabelValue == null || key.equals(Dist.META_LABEL) || !value.equals(defLabelValue)) {
                    dist.meta(key, value);
                }
            }
        }

        @Override
        public void stop() throws IOException {
        }

        public static String[] split(String string) {
            return string.replaceAll("\\p{Space}*'(.*)'\\p{Space}*", "$1").split("'\\p{Space}+'");
        }

        private static int extract(String string, Pattern pattern) {
            final Matcher matcher = pattern.matcher(string);
            if (matcher.matches()) {
                return Integer.parseInt(matcher.group(1));
            }
            return 0;
        }
    }
}
