package io.github.eterverda.playless.common.json;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import io.github.eterverda.playless.common.Link;
import io.github.eterverda.playless.common.Repo;

public class JsonRepoFactory {
    public static final JsonRepoFactory INSTANCE = new JsonRepoFactory();

    public static JsonRepoFactory getInstance() {
        return INSTANCE;
    }

    @NotNull
    public Repo loadDecorated(JsonReader in) throws IOException {
        in.beginObject();
        final String decor = in.nextName();
        if (!decor.equals(JsonConstants.DECOR_PLAYLESS_REPOSITORY_V1)) {
            throw new IllegalArgumentException("Expected " + JsonConstants.DECOR_PLAYLESS_REPOSITORY_V1);
        }

        final Repo result = load(in);

        in.endObject();

        return result;
    }

    @NotNull
    public Repo load(JsonReader in) throws IOException {
        final Repo.Editor result = new Repo.Editor();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case JsonConstants.DISTRIBUTIONS:
                    loadDists(in, result);
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

    public void loadDists(JsonReader in, Repo.Editor result) throws IOException {
        final JsonDistFactory factory = JsonDistFactory.getInstance();

        in.beginArray();
        while (in.hasNext()) {
            result.dist(factory.load(in));
        }
        in.endArray();
    }


    private void loadLinks(JsonReader in, Repo.Editor result) throws IOException {
        in.beginArray();
        while (in.hasNext()) {
            result.link(loadLink(in));
        }
        in.endArray();
    }
    private void loadMeta(JsonReader in, Repo.Editor result) throws IOException {
        in.beginObject();
        while (in.hasNext()) {
            result.meta(in.nextName(), in.nextString());
        }
        in.endObject();
    }

    @NotNull
    static Link loadLink(JsonReader in) throws IOException {
        String rel = null;
        String href = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case JsonConstants.REL:
                    rel = in.nextString();
                    break;
                case JsonConstants.HREF:
                    href = in.nextString();
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();

        if (rel == null) {
            throw new IllegalArgumentException("rel not found for link");
        }
        if (href == null) {
            throw new IllegalArgumentException("href not found for link");
        }

        return new Link(rel, href);
    }
}
