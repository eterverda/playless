package io.github.eterverda.playless.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.JsonConstants;
import io.github.eterverda.playless.common.util.TimestampUtils;

public class JsonDistDumper {
    private final JsonGenerator generator;

    public JsonDistDumper(OutputStream out) throws IOException {
        generator = new JsonFactory().createGenerator(out);
    }

    public void setPrettyPrint(boolean pretty) {
        generator.setPrettyPrinter(pretty ? new DefaultPrettyPrinter() : null);
    }

    public void write(Dist... dists) throws IOException {
        generator.writeStartArray();
        for (Dist dist : dists) {
            writeSingle(dist);
        }
        generator.writeEndArray();

        generator.flush();
    }

    public void writeSingle(Dist dist) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField(JsonConstants.APPLICATION_ID, dist.applicationId);

        generator.writeFieldName(JsonConstants.VERSION);
        write(dist.version);

        generator.writeFieldName(JsonConstants.FILTER);
        write(dist.filter);

        if (!dist.meta.isEmpty()) {
            generator.writeFieldName(JsonConstants.META);
            write(dist.meta);
        }

        generator.writeEndObject();

        generator.flush();
    }

    private void write(Dist.Version version) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField(JsonConstants.VERSION_CODE, version.versionCode);
        if (version.timestamp != Long.MIN_VALUE) {
            generator.writeObjectField(JsonConstants.TIMESTAMP, TimestampUtils.zulu(version.timestamp));
        }
        if (version.fingerprint != null) {
            generator.writeObjectField(JsonConstants.FINGERPRINT_PREFIX + version.fingerprint.getShortAlgorithm(), version.fingerprint.getStringValue());
        }
        if (version.signatures != null) {
            generator.writeObjectField(JsonConstants.SIGNATURES_PREFIX + version.signatures.getShortAlgorithm(), version.signatures.getStringValue());
        }
        if (version.debug) {
            generator.writeObjectField(JsonConstants.DEBUG, true);
        }

        generator.writeEndObject();
    }

    private void write(Map<String, String> meta) throws IOException {
        generator.writeStartObject();

        for (Map.Entry<String, String> entry : meta.entrySet()) {
            if (entry.getValue() != null) {
                generator.writeObjectField(entry.getKey(), entry.getValue());
            }
        }

        generator.writeEndObject();
    }

    private void write(Dist.Filter filter) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField(JsonConstants.MIN_SDK_VERSION, filter.minSdkVersion);

        if (filter.maxSdkVersion != Integer.MAX_VALUE) {
            generator.writeObjectField(JsonConstants.MAX_SDK_VERSION, filter.maxSdkVersion);
        }

        if (filter.requiresSmallestWidthDp > 0) {
            generator.writeObjectField(JsonConstants.REQUIRES_SMALLEST_WIDTH_DP, filter.requiresSmallestWidthDp);
        }

        if (filter.usesGlEs > 0) {
            generator.writeObjectField(JsonConstants.USES_GL_ES, String.format("0x%h", filter.usesGlEs));
        }

        generator.writeFieldName(JsonConstants.SUPPORTS_SCREENS);
        write(filter.supportsScreens);

        generator.writeFieldName(JsonConstants.COMPATIBLE_SCREENS);
        write(filter.compatibleScreens);

        generator.writeFieldName(JsonConstants.SUPPORTS_GL_TEXTURES);
        write(filter.supportsGlTextures);

        generator.writeFieldName(JsonConstants.USES_FEATURES);
        write(filter.usesFeatures);

        generator.writeFieldName(JsonConstants.USES_CONFIGURATIONS);
        write(filter.usesConfigurations);

        generator.writeFieldName(JsonConstants.USES_LIBRARIES);
        write(filter.usesLibraries);

        if (!filter.nativeCode.isEmpty()) {
            generator.writeFieldName(JsonConstants.NATIVE_CODE);
            write(filter.nativeCode);
        }

        generator.writeEndObject();
    }

    private void write(Collection<String> collection) throws IOException {
        generator.writeStartArray();
        for (String supportsScreen : collection) {
            generator.writeString(supportsScreen);
        }
        generator.writeEndArray();
    }
}
