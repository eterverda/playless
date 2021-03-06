package io.github.eterverda.playless.common.json;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Set;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.Repo;
import io.github.eterverda.playless.common.util.TimestampUtils;
import io.github.eterverda.util.checksum.Checksum;

public class JsonDistFactory {
    public static final JsonDistFactory INSTANCE = new JsonDistFactory();

    public static JsonDistFactory getInstance() {
        return INSTANCE;
    }

    @NotNull
    public Dist[] loadDecorated(JsonReader in) throws IOException {
        final Repo repo = JsonRepoFactory.getInstance().loadDecorated(in);
        final Set<Dist> dists = repo.dists;
        return dists.toArray(new Dist[dists.size()]);
    }

    @NotNull
    public Dist load(JsonReader in) throws IOException {
        final Dist.Editor result = new Dist.Editor();

        in.beginObject();
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
                case JsonConstants.LINKS:
                    loadLinks(in, result);
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
            switch (in.nextName()) {
                case JsonConstants.VERSION_CODE:
                    result.versionCode(in.nextInt());
                    break;
                case JsonConstants.TIMESTAMP:
                    result.timestamp(TimestampUtils.zulu(in.nextString()));
                    break;
                case JsonConstants.DEBUG:
                    result.debug(in.nextBoolean());
                    break;
                case JsonConstants.FINGERPRINT:
                    result.fingerprint(createChecksum(in.nextString()));
                    break;
                case JsonConstants.SIGNATURES:
                    result.signatures(createChecksum(in.nextString()));
                    break;
                default:
                    in.skipValue();
                    break;
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
                default:
                    in.skipValue();
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

    private void loadLinks(JsonReader in, Dist.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.link(JsonRepoFactory.loadLink(in));
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

    private static Checksum createChecksum(String checksumStr) {
        try {
            final int indexOfColon = checksumStr.indexOf(':');
            final String algorithm = checksumStr.substring(0, indexOfColon);
            final String hexValue = checksumStr.substring(indexOfColon + 1);

            return new Checksum(algorithm, hexValue);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
