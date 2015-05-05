package io.github.eterverda.playless.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class Repo {
    public final Set<Dist> dists;
    public final Set<Link> links;
    public final Map<String, String> meta;

    protected Repo(
            Set<Dist> dists,
            Set<Link> links,
            Map<String, String> meta) {

        this.dists = dists;
        this.links = links;
        this.meta = meta;
    }

    public Editor edit() {
        return new Editor(this);
    }

    public static final class Editor {
        private Set<Dist> dists;
        private Set<Link> links;
        private Map<String, String> meta;

        public Editor() {
            dists = Collections.emptySet();
            links = Collections.emptySet();
            meta = Collections.emptyMap();
        }

        protected Editor(Repo repo) {
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

        public void dist(Dist dist) {
            dists = modifiableLinkedHashSet(dists);
            dists.add(dist);
        }

        public void link(Link link) {
            links = modifiableTreeSet(links);
            links.add(link);
        }

        public void link(String rel, String href) {
            link(new Link(rel, href));
        }

        public void unlink(Link link) {
            links = modifiableTreeSet(links);
            links.remove(link);
        }

        public void meta(String key, String value) {
            meta = modifiableTreeMap(meta);
            meta.put(key, value);
        }

        static <T> TreeSet<T> modifiableTreeSet(Set<T> set) {
            return set instanceof TreeSet ? (TreeSet<T>) set : new TreeSet<>(set);
        }

        static <T> HashSet<T> modifiableHashSet(Set<T> set) {
            return set instanceof HashSet ? (HashSet<T>) set : new HashSet<>(set);
        }

        static <T> LinkedHashSet<T> modifiableLinkedHashSet(Set<T> set) {
            return set instanceof LinkedHashSet ? (LinkedHashSet<T>) set : new LinkedHashSet<>(set);
        }

        static <K, V> TreeMap<K, V> modifiableTreeMap(Map<K, V> meta) {
            return meta instanceof TreeMap ? (TreeMap<K, V>) meta : new TreeMap<>(meta);
        }

        static <T> Set<T> unmodifiableSet(Set<T> set) {
            return set.isEmpty() ? Collections.<T>emptySet() : Collections.unmodifiableSet(set);
        }

        static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
            return map.isEmpty() ? Collections.<K, V>emptyMap() : Collections.unmodifiableMap(map);
        }
    }
}
