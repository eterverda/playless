package io.github.eterverda.playless.core;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import java.io.IOException;
import java.io.OutputStream;

import io.github.eterverda.playless.common.Distribution;

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
            generator.writeObjectField("timestamp", dist.timestamp());
        }
        if (dist.sha1() != null) {
            generator.writeObjectField("sha1", dist.sha1());
        }

        generator.writeFieldName("selector");
        write(dist.selector());

        generator.writeFieldName("filter");
        write(dist.filter());

        generator.writeEndObject();

        generator.flush();
    }

    private void write(Distribution.Selector selector) throws IOException {
        generator.writeStartObject();

        generator.writeObjectField("minSdkVersion", selector.minSdkVersion());
        if (selector.maxSdkVersion() != Integer.MAX_VALUE) {
            generator.writeObjectField("maxSdkVersion", selector.maxSdkVersion());
        }

        generator.writeArrayFieldStart("supportsScreens");
        for (String supportsScreen : selector.supportsScreens()) {
            generator.writeString(supportsScreen);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("compatibleScreens");
        for (String compatibleScreen : selector.compatibleScreens()) {
            generator.writeString(compatibleScreen);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("supportsGlTextures");
        for (String supportsGlTexture : selector.supportsGlTextures()) {
            generator.writeString(supportsGlTexture);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("usesFeatures");
        for (String usesFeature : selector.usesFeatures()) {
            generator.writeString(usesFeature);
        }
        generator.writeEndArray();

        if (!selector.abis().isEmpty()) {
            generator.writeArrayFieldStart("abis");
            for (String abi : selector.abis()) {
                generator.writeString(abi);
            }
            generator.writeEndArray();
        }

        generator.writeEndObject();
    }

    private void write(Distribution.Filter filter) throws IOException {
        generator.writeStartObject();

        generator.writeArrayFieldStart("usesConfigurations");
        for (String usesConfiguration : filter.usesConfigurations()) {
            generator.writeString(usesConfiguration);
        }
        generator.writeEndArray();

        generator.writeArrayFieldStart("usesLibraries");
        for (String usesLibrary : filter.usesLibraries()) {
            generator.writeString(usesLibrary);
        }
        generator.writeEndArray();

        generator.writeEndObject();
    }
}
