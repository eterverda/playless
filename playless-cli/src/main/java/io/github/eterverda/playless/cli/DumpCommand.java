package io.github.eterverda.playless.cli;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteStreamHandler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.playless.core.JsonDistributionDumper;
import io.github.eterverda.playless.core.Repository;
import io.github.eterverda.util.checksum.Checksum;

public class DumpCommand implements Command {
    private static final byte[] NULL = new byte[8192];

    private static final Pattern VERSION_CODE = Pattern.compile("package:.* versionCode='([0-9]*).*'");
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
    private static final Pattern SUPPORTS_SCREENS = Pattern.compile("supports-screens:(.*)");
    private static final Pattern REQUIRES_SMALLEST_WIDTH = Pattern.compile("requires-smallest-width:'(\\p{Digit}*)'");
    private static final Pattern COMPATIBLE_SCREENS = Pattern.compile("compatible-screens:(.*)");
    private static final Pattern SUPPORTS_GL_TEXTURES = Pattern.compile("supports-gl-texture:'(.*)'");
    private static final Pattern USES_LIBRARY = Pattern.compile("uses-library:'(.*)'");
    private static final Pattern ABIS = Pattern.compile("native-code:(.*)");

    @Override
    public void main(Repository repo, String[] rawArgs) {
        final ArrayList<String> args = new ArrayList<>(rawArgs.length);
        Collections.addAll(args, rawArgs);

        try {
            main(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void main(ArrayList<String> args) throws IOException {
        final CommandLine aapt = new CommandLine(extractAapt(args))
                .addArgument("dump")
                .addArgument("badging");

        for (String arg : args) {
            final Distribution.Builder builder = new Distribution.Builder();

            System.out.printf("src : \"%s\",\n", arg);

            timestamp(builder, arg);
            aapt(builder, aapt, arg);
            fingerprint(builder, arg);
            signatures(builder, arg);

            final Distribution dist = builder.build();

            System.out.printf("apk : \"%s\",\n", dist.apkName());
            System.out.printf("dst : ");
            final JsonDistributionDumper dumper = new JsonDistributionDumper(System.out);
            dumper.write(dist);
            System.out.println();
        }
    }

    private void timestamp(Distribution.Builder builder, String arg) {
        builder.timestamp(new File(arg).lastModified());
    }

    private static void aapt(Distribution.Builder dist, CommandLine aapt, String arg) throws IOException {
        final DistributionHandler handler = new DistributionHandler(dist);

        final DefaultExecutor executor = new DefaultExecutor();
        executor.setStreamHandler(handler);

        final CommandLine line = new CommandLine(aapt).addArgument(arg);
        executor.execute(line);
    }

    private static void fingerprint(Distribution.Builder dist, String arg) throws IOException {
        try (FileInputStream in = new FileInputStream(new File(arg))) {
            final Checksum fingerprint = Checksum.sha1(in);
            dist.fingerprint(fingerprint);
        }
    }

    private static void signatures(Distribution.Builder dist, String arg) throws IOException {
        try (JarFile jar = new JarFile(arg)) {
            final JarEntry androidManifest = jar.getJarEntry("AndroidManifest.xml");

            try (InputStream in = jar.getInputStream(androidManifest)) {
                consume(in);
            }

            signatures(dist, androidManifest.getCertificates());
        }
    }

    private static void signatures(Distribution.Builder dist, Certificate[] certificates) {
        for (Certificate certificate : certificates) {
            signature(dist, certificate);
        }
    }

    private static void signature(Distribution.Builder dist, Certificate certificate) {
        try {
            final Checksum signature = Checksum.sha1(certificate.getEncoded());
            dist.signature(signature);
        } catch (CertificateEncodingException e) {
            throw new AssertionError(e);
        }
    }

    private static void consume(InputStream in) throws IOException {
        //noinspection StatementWithEmptyBody
        while (in.read(NULL) != -1) {
            // we just consume data
        }
    }

    private static class DistributionHandler implements ExecuteStreamHandler {
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

    private static String extractAapt(ArrayList<String> args) {
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            if (arg.startsWith("--aapt=")) {
                args.remove(i);
                return arg.substring(7);
            }
            if (arg.equals("--aapt")) {
                if (args.size() == i - 1) {
                    throw new IllegalArgumentException("No value for option " + arg);
                }
                args.remove(i);
                return args.remove(i);
            }
        }
        return null;
    }
}
