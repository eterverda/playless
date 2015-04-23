package io.github.eterverda.playless.common.json;

import java.io.IOException;

/**
 * This is adapter interface to streaming json parser. It mimics both gson's and android's
 * JsonReader (which in fact are the same).
 */
public interface JsonReader {
    void beginArray() throws IOException;

    void endArray() throws IOException;

    void beginObject() throws IOException;

    void endObject() throws IOException;

    boolean hasNext() throws IOException;

    String nextName() throws IOException;

    int nextInt() throws IOException;

    String nextString() throws IOException;

    boolean nextBoolean() throws IOException;

    void skipValue() throws IOException;
}
