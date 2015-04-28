package io.github.eterverda.playless.core.json;

import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;
import java.util.Map;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.json.JsonConstants;
import io.github.eterverda.playless.common.util.TimestampUtils;

public class JsonDistDumper {
    private static final String MY_VERSION = "1";

    private final JsonWriter out;

    public JsonDistDumper(OutputStream out) throws IOException {
        this.out = new JsonWriter(new OutputStreamWriter(out));
    }

    public void setPrettyPrint(boolean pretty) {
        out.setIndent(pretty ? "  " : "");
    }

    public void writeDecorated(Dist... dists) throws IOException {
        out.beginObject();

        out.name(JsonConstants.VERSION).value(MY_VERSION);

        out.name(JsonConstants.DISTRIBUTIONS);
        writeList(dists);

        out.endObject();

        out.flush();
    }

    public void writeList(Dist... dists) throws IOException {
        out.beginArray();
        for (Dist dist : dists) {
            write(dist);
        }
        out.endArray();

        out.flush();
    }

    public void write(Dist dist) throws IOException {
        out.beginObject();

        out.name(JsonConstants.APPLICATION_ID).value(dist.applicationId);

        out.name(JsonConstants.VERSION);
        write(dist.version);

        out.name(JsonConstants.FILTER);
        write(dist.filter);

        if (!dist.meta.isEmpty()) {
            out.name(JsonConstants.META);
            write(dist.meta);
        }

        out.endObject();

        out.flush();
    }

    private void write(Dist.Version version) throws IOException {
        out.beginObject();

        out.name(JsonConstants.VERSION_CODE).value(version.versionCode);
        if (version.timestamp != Long.MIN_VALUE) {
            out.name(JsonConstants.TIMESTAMP).value(TimestampUtils.zulu(version.timestamp));
        }
        if (version.fingerprint != null) {
            out.name(JsonConstants.FINGERPRINT_PREFIX + version.fingerprint.getShortAlgorithm()).value(version.fingerprint.getStringValue());
        }
        if (version.signatures != null) {
            out.name(JsonConstants.SIGNATURES_PREFIX + version.signatures.getShortAlgorithm()).value(version.signatures.getStringValue());
        }
        if (version.debug) {
            out.name(JsonConstants.DEBUG).value(true);
        }

        out.endObject();
    }

    private void write(Map<String, String> meta) throws IOException {
        out.beginObject();

        for (Map.Entry<String, String> entry : meta.entrySet()) {
            if (entry.getValue() != null) {
                out.name(entry.getKey()).value(entry.getValue());
            }
        }

        out.endObject();
    }

    private void write(Dist.Filter filter) throws IOException {
        out.beginObject();

        out.name(JsonConstants.MIN_SDK_VERSION).value(filter.minSdkVersion);

        if (filter.maxSdkVersion != Integer.MAX_VALUE) {
            out.name(JsonConstants.MAX_SDK_VERSION).value(filter.maxSdkVersion);
        }

        if (filter.requiresSmallestWidthDp > 0) {
            out.name(JsonConstants.REQUIRES_SMALLEST_WIDTH_DP).value(filter.requiresSmallestWidthDp);
        }

        if (filter.usesGlEs > 0) {
            out.name(JsonConstants.USES_GL_ES).value(String.format("0x%h", filter.usesGlEs));
        }

        out.name(JsonConstants.SUPPORTS_SCREENS);
        write(filter.supportsScreens);

        out.name(JsonConstants.COMPATIBLE_SCREENS);
        write(filter.compatibleScreens);

        out.name(JsonConstants.SUPPORTS_GL_TEXTURES);
        write(filter.supportsGlTextures);

        out.name(JsonConstants.USES_FEATURES);
        write(filter.usesFeatures);

        out.name(JsonConstants.USES_CONFIGURATIONS);
        out.beginArray();
        for (Dist.Filter.Config usesConfiguration : filter.usesConfigurations) {
            write(usesConfiguration);
        }
        out.endArray();

        out.name(JsonConstants.USES_LIBRARIES);
        write(filter.usesLibraries);

        if (!filter.nativeCode.isEmpty()) {
            out.name(JsonConstants.NATIVE_CODE);
            write(filter.nativeCode);
        }

        out.endObject();
    }

    private void write(Dist.Filter.Config config) throws IOException {
        out.beginObject();
        writeConfigurationField(JsonConstants.FIVE_WAY_NAV, config.fiveWayNav);
        writeConfigurationField(JsonConstants.HARD_KEYBOARD, config.hardKeyboard);
        writeConfigurationField(JsonConstants.KEYBOARD_TYPE, config.keyboardType);
        writeConfigurationField(JsonConstants.NAVIGATION, config.navigation);
        writeConfigurationField(JsonConstants.TOUCH_SCREEN, config.touchScreen);
        out.endObject();
    }

    private void writeConfigurationField(String fieldName, int fieldValue) throws IOException {
        if (fieldValue == 0) {
            return;
        }
        out.name(fieldName).value(fieldValue);
    }

    private void write(Collection<String> collection) throws IOException {
        out.beginArray();
        for (String supportsScreen : collection) {
            out.value(supportsScreen);
        }
        out.endArray();
    }
}
