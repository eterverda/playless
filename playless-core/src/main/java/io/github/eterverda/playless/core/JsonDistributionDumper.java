package io.github.eterverda.playless.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import io.github.eterverda.playless.common.Distribution;
import io.github.eterverda.playless.common.util.TimestampUtils;

public class JsonDistributionDumper {
    private final JsonGenerator generator;

    public JsonDistributionDumper(OutputStream out) throws IOException {
        generator = new JsonFactory().createGenerator(out);
        generator.setPrettyPrinter(new DefaultPrettyPrinter());
    }

    public void write(Distribution dist) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("applicationId", dist.applicationId());
        generator.writeObjectField("versionCode", dist.versionCode());
        if (dist.timestamp() != Long.MIN_VALUE) {
            generator.writeObjectField("timestamp", TimestampUtils.zulu(dist.timestamp()));
        }
        if (dist.fingerprint() != null) {
            generator.writeObjectField("fingerprint-" + dist.fingerprint().getShortAlgorithm(), dist.fingerprint().getStringValue());
        }
        if (dist.signatures() != null) {
            generator.writeObjectField("signatures-" + dist.signatures().getShortAlgorithm(), dist.signatures().getStringValue());
        }
        if (dist.debug()) {
            generator.writeObjectField("debug", true);
        }

        generator.writeFieldName("meta");
        write(dist.meta());

        generator.writeFieldName("requirements");
        write(dist.requirements());

        generator.writeEndObject();

        generator.flush();
    }

    private void write(Map<String, String> meta) throws IOException {
        generator.writeStartObject();

        for (Map.Entry<String, String> entry : meta.entrySet()) {
            generator.writeObjectField(entry.getKey(), entry.getValue());
        }

        generator.writeEndObject();
    }

    private void write(Distribution.Requirements requirements) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("minSdkVersion", requirements.minSdkVersion());
        if (requirements.maxSdkVersion() != Integer.MAX_VALUE) {
            generator.writeObjectField("maxSdkVersion", requirements.maxSdkVersion());
        }

        generator.writeArrayFieldStart("supportsScreens");
        for (String supportsScreen : requirements.supportsScreens()) {
            generator.writeString(supportsScreen);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("compatibleScreens");
        for (String compatibleScreen : requirements.compatibleScreens()) {
            generator.writeString(compatibleScreen);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("supportsGlTextures");
        for (String supportsGlTexture : requirements.supportsGlTextures()) {
            generator.writeString(supportsGlTexture);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("usesFeatures");
        for (String usesFeature : requirements.usesFeatures()) {
            generator.writeString(usesFeature);
        }
        generator.writeEndArray();

        if (!requirements.abis().isEmpty()) {
            generator.writeArrayFieldStart("abis");
            for (String abi : requirements.abis()) {
                generator.writeString(abi);
            }
            generator.writeEndArray();
        }

        generator.writeArrayFieldStart("usesConfigurations");
        for (String usesConfiguration : requirements.usesConfigurations()) {
            generator.writeString(usesConfiguration);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("usesLibraries");
        for (String usesLibrary : requirements.usesLibraries()) {
            generator.writeString(usesLibrary);
        }
        generator.writeEndArray();

        generator.writeEndObject();
    }
}
