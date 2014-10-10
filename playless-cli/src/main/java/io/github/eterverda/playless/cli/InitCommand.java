package io.github.eterverda.playless.cli;

import io.github.eterverda.playless.core.Repository;

public class InitCommand implements Command {
    @Override
    public void main(Repository repo, String[] args) {
        System.out.println("Initialized repository in " + repo.getRootDir());
    }
}
