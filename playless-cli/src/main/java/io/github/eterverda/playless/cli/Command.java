package io.github.eterverda.playless.cli;

import io.github.eterverda.playless.core.Repository;

public interface Command {
    void main(Repository repo, String[] args);
}
