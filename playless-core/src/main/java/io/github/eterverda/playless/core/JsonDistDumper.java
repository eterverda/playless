package io.github.eterverda.playless.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import io.github.eterverda.playless.common.Dist;
import io.github.eterverda.playless.common.util.TimestampUtils;

public class JsonDistDumper {
    private final JsonGenerator generator;

    public JsonDistDumper(OutputStream out) throws IOException {
        generator = new JsonFactory().createGenerator(out);
        generator.setPrettyPrinter(new DefaultPrettyPrinter());
    }

    public void write(Dist dist) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("applicationId", dist.applicationId);

        generator.writeFieldName("version");
        write(dist.version);

        generator.writeFieldName("filter");
        write(dist.filter);

        if (!dist.meta.isEmpty()) {
            generator.writeFieldName("meta");
            write(dist.meta);
        }

        generator.writeEndObject();

        generator.flush();
    }

    private void write(Dist.Version version) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("versionCode", version.versionCode);
        if (version.timestamp != Long.MIN_VALUE) {
            generator.writeObjectField("timestamp", TimestampUtils.zulu(version.timestamp));
        }
        if (version.fingerprint != null) {
            generator.writeObjectField("fingerprint-" + version.fingerprint.getShortAlgorithm(), version.fingerprint.getStringValue());
        }
        if (version.signatures != null) {
            generator.writeObjectField("signatures-" + version.signatures.getShortAlgorithm(), version.signatures.getStringValue());
        }
        if (version.debug) {
            generator.writeObjectField("debug", true);
        }

        generator.writeEndObject();
    }

    private void write(Map<String, String> meta) throws IOException {
        generator.writeStartObject();

        for (Map.Entry<String, String> entry : meta.entrySet()) {
            generator.writeObjectField(entry.getKey(), entry.getValue());
        }

        generator.writeEndObject();
    }

    private void write(Dist.Filter filter) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("minSdkVersion", filter.minSdkVersion);

        if (filter.maxSdkVersion != Integer.MAX_VALUE) {
            generator.writeObjectField("maxSdkVersion", filter.maxSdkVersion);
        }

        generator.writeFieldName("supportsScreens");
        write(filter.supportsScreens);

        generator.writeFieldName("compatibleScreens");
        write(filter.compatibleScreens);

        generator.writeFieldName("supportsGlTextures");
        write(filter.supportsGlTextures);

        generator.writeFieldName("usesFeatures");
        write(filter.usesFeatures);

        generator.writeFieldName("usesConfigurations");
        write(filter.usesConfigurations);

        generator.writeFieldName("usesLibraries");
        write(filter.usesLibraries);

        if (!filter.nativeCode.isEmpty()) {
            generator.writeFieldName("nativeCode");
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
