package io.github.eterverda.playless.lib;

import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonReader;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.JsonConstants;
import io.github.eterverda.playless.common.util.TimestampUtils;
import io.github.eterverda.util.checksum.Checksum;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class JsonDistFactory {

    public Dist[] load(String dists) {
        try {
            return load(new StringReader(dists));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    private Dist[] load(Reader in) throws IOException {
        return load(new JsonReader(in));
    }

    private Dist[] load(JsonReader in) throws IOException {
        final ArrayList<Dist> result = new ArrayList<>();

        in.beginArray();
        while (in.hasNext()) {
            result.add(loadSingle(in));
        }
        in.endArray();

        return result.toArray(new Dist[result.size()]);
    }

    private Dist loadSingle(JsonReader in) throws IOException {
        in.beginObject();

        final Dist.Editor result = new Dist.Editor();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case JsonConstants.APPLICATION_ID:
                    result.applicationId(in.nextString());
                    break;
                case JsonConstants.VERSION:
                    loadVersion(in, result);
                    break;
                case JsonConstants.FILTER:
                    loadFilter(in, result);
                    break;
                case JsonConstants.META:
                    loadMeta(in, result);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();

        return result.build();
    }

    private void loadVersion(JsonReader in, Dist.Editor result) throws IOException {
        in.beginObject();
        while (in.hasNext()) {
            final String name = in.nextName();
            if (name.equals(JsonConstants.VERSION_CODE)) {
                result.versionCode(in.nextInt());

            } else if (name.equals(JsonConstants.TIMESTAMP)) {
                final long timestamp = TimestampUtils.zulu(in.nextString());
                result.timestamp(timestamp);

            } else if (name.equals(JsonConstants.DEBUG)) {
                result.debug(in.nextBoolean());

            } else if (name.startsWith(JsonConstants.FINGERPRINT_PREFIX)) {
                final String algorithm = name.substring(JsonConstants.FINGERPRINT_PREFIX.length());
                final String value = in.nextString();
                result.fingerprint(createChecksum(algorithm, value));

            } else if (name.startsWith(JsonConstants.SIGNATURES_PREFIX)) {
                final String algorithm = name.substring(JsonConstants.SIGNATURES_PREFIX.length());
                final String value = in.nextString();
                result.signatures(createChecksum(algorithm, value));
            }
        }
        in.endObject();
    }

    private void loadFilter(JsonReader in, Dist.Editor result) throws IOException {
        in.beginObject();

        while (in.hasNext()) {
            switch (in.nextName()) {
                case JsonConstants.MIN_SDK_VERSION:
                    result.minSdkVersion(in.nextInt());
                    break;
                case JsonConstants.MAX_SDK_VERSION:
                    result.maxSdkVersion(in.nextInt());
                    break;
                case JsonConstants.REQUIRES_SMALLEST_WIDTH_DP:
                    result.requiresSmallestWidthDp(in.nextInt());
                    break;
                case JsonConstants.USES_GL_ES:
                    result.usesGlEs(Integer.parseInt(in.nextString().substring(2)));
                    break;
                case JsonConstants.SUPPORTS_SCREENS:
                    loadSupportsScreens(in, result);
                    break;
                case JsonConstants.SUPPORTS_GL_TEXTURES:
                    loadSupportsGlTextures(in, result);
                    break;
                case JsonConstants.COMPATIBLE_SCREENS:
                    loadCompatibleScreens(in, result);
                    break;
                case JsonConstants.USES_FEATURES:
                    loadUsesFeatures(in, result);
                    break;
                case JsonConstants.USES_CONFIGURATIONS:
                    loadUsesConfigurations(in, result);
                    break;
                case JsonConstants.USES_LIBRARIES:
                    loadUsesLibraries(in, result);
                    break;
                case JsonConstants.NATIVE_CODE:
                    loadNativeCode(in, result);
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }

        in.endObject();
    }
    private void loadSupportsScreens(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.supportsScreen(in.nextString());
        }
        in.endArray();
    }


    private void loadSupportsGlTextures(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.supportsGlTexture(in.nextString());
        }
        in.endArray();
    }

    private void loadCompatibleScreens(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.compatibleScreen(in.nextString());
        }
        in.endArray();
    }

    private void loadUsesFeatures(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.usesFeature(in.nextString());
        }
        in.endArray();
    }

    private void loadUsesConfigurations(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            loadUsesConfiguration(in, result);
        }
        in.endArray();
    }

    private void loadUsesConfiguration(JsonReader in, Dist.Editor result) throws IOException {
        in.beginObject();

        int fiveWayNav = 0;
        int hardKeyboard = 0;
        int keyboardType = 0;
        int navigation = 0;
        int touchScreen = 0;
        while (in.hasNext()) {
            switch (in.nextName()) {
                case JsonConstants.FIVE_WAY_NAV:
                    fiveWayNav = in.nextInt();
                    break;
                case JsonConstants.HARD_KEYBOARD:
                    hardKeyboard = in.nextInt();
                    break;
                case JsonConstants.KEYBOARD_TYPE:
                    keyboardType = in.nextInt();
                    break;
                case JsonConstants.NAVIGATION:
                    navigation = in.nextInt();
                    break;
                case JsonConstants.TOUCH_SCREEN:
                    touchScreen = in.nextInt();
                    break;
            }
        }
        result.usesConfiguration(fiveWayNav, hardKeyboard, keyboardType, navigation, touchScreen);

        in.endObject();
    }

    private void loadUsesLibraries(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.usesLibrary(in.nextString());
        }
        in.endArray();
    }

    private void loadNativeCode(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.nativeCode(in.nextString());
        }
        in.endArray();
    }

    private void loadMeta(JsonReader in, Dist.Editor result) throws IOException {
        in.beginObject();
        while (in.hasNext()) {
            result.meta(in.nextName(), in.nextString());
        }
        in.endObject();
    }

    private static Checksum createChecksum(String algorithm, String value) {
        try {
            return new Checksum(algorithm, value);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
