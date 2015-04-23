package io.github.eterverda.playless.lib.json;

import android.annotation.TargetApi;
import android.os.Build;

import java.io.IOException;

import io.github.eterverda.playless.common.json.JsonReader;

/**
 * {@link io.github.eterverda.playless.common.json.JsonReader} feat. {@link android.util.JsonReader}
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public final class AndroidJsonReader implements JsonReader {
    private final android.util.JsonReader impl;

    public AndroidJsonReader(android.util.JsonReader impl) {
        this.impl = impl;
    }

    @Override
    public void beginArray() throws IOException {
        impl.beginArray();
    }

    @Override
    public void endArray() throws IOException {
        impl.endArray();
    }

    @Override
    public void beginObject() throws IOException {
        impl.beginObject();
    }

    @Override
    public void endObject() throws IOException {
        impl.endObject();
    }

    @Override
    public boolean hasNext() throws IOException {
        return impl.hasNext();
    }

    @Override
    public String nextName() throws IOException {
        return impl.nextName();
    }

    @Override
    public int nextInt() throws IOException {
        return impl.nextInt();
    }

    @Override
    public String nextString() throws IOException {
        return impl.nextString();
    }

    @Override
    public boolean nextBoolean() throws IOException {
        return impl.nextBoolean();
    }

    @Override
    public void skipValue() throws IOException {
        impl.endObject();
    }
}
