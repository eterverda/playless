package io.github.eterverda.playless.common;

import net.jcip.annotations.Immutable;
import net.jcip.annotations.NotThreadSafe;
import net.jcip.annotations.ThreadSafe;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Immutable
@ThreadSafe
public final class Repo {
    @NotNull
    public final Set<Dist> dists;
    @NotNull
    public final Set<Link> links;
    @NotNull
    public final Map<String, String> meta;

    protected Repo(
            @NotNull Set<Dist> dists,
            @NotNull Set<Link> links,
            @NotNull Map<String, String> meta) {

        this.dists = dists;
        this.links = links;
        this.meta = meta;
    }

    @NotNull
    public Editor edit() {
        return new Editor(this);
    }

    @NotThreadSafe
    public static final class Editor {
        private Set<Dist> dists;
        private Set<Link> links;
        private Map<String, String> meta;

        public Editor() {
            dists = Collections.emptySet();
            links = Collections.emptySet();
            meta = Collections.emptyMap();
        }

        protected Editor(@NotNull Repo repo) {
            dists = repo.dists;
            links = repo.links;
            meta = repo.meta;
        }

        public Repo build() {
            dists = unmodifiableSet(dists);
            links = unmodifiableSet(links);
            meta = unmodifiableMap(meta);

            return new Repo(dists, links, meta);
        }

        public void dist(@NotNull Dist dist) {
            dists = modifiableLinkedHashSet(dists);
            dists.add(dist);
        }

        public void link(@NotNull Link link) {
            links = modifiableTreeSet(links);
            links.add(link);
        }

        public void link(@NotNull String rel, @NotNull String href) {
            link(new Link(rel, href));
        }

        public void unlink(@NotNull Link link) {
            links = modifiableTreeSet(links);
            links.remove(link);
        }

        public void meta(@NotNull String key, @NotNull String value) {
            meta = modifiableTreeMap(meta);
            meta.put(key, value);
        }

        @NotNull
        static <T> TreeSet<T> modifiableTreeSet(@NotNull Set<T> set) {
            return set instanceof TreeSet ? (TreeSet<T>) set : new TreeSet<>(set);
        }

        @NotNull
        static <T> HashSet<T> modifiableHashSet(@NotNull Set<T> set) {
            return set instanceof HashSet ? (HashSet<T>) set : new HashSet<>(set);
        }

        @NotNull
        static <T> LinkedHashSet<T> modifiableLinkedHashSet(@NotNull Set<T> set) {
            return set instanceof LinkedHashSet ? (LinkedHashSet<T>) set : new LinkedHashSet<>(set);
        }

        @NotNull
        static <K, V> TreeMap<K, V> modifiableTreeMap(@NotNull Map<K, V> meta) {
            return meta instanceof TreeMap ? (TreeMap<K, V>) meta : new TreeMap<>(meta);
        }

        @NotNull
        static <T> Set<T> unmodifiableSet(@NotNull Set<T> set) {
            return set.isEmpty() ? Collections.<T>emptySet() : Collections.unmodifiableSet(set);
        }

        @NotNull
        static <K, V> Map<K, V> unmodifiableMap(@NotNull Map<K, V> map) {
            return map.isEmpty() ? Collections.<K, V>emptyMap() : Collections.unmodifiableMap(map);
        }
    }
}
