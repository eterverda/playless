package io.github.eterverda.playless.cli;

import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;

import java.io.File;

import io.github.eterverda.playless.core.PlaylessRepositoryException;
import io.github.eterverda.playless.core.Repository;

public class InitCommand implements Command {
    public static Subparser addSubParser(ArgumentParser parser) {
        final Subparser subparser = parser.addSubparsers().addParser("init").help("initializes playless repository");
        Main.addRepoArgument(subparser);
        return subparser;
    }

    @Override
    public void main(Namespace args) {
        final File path = args.get("repo");
        try {
            final Repository repo = Repository.init(path);
            System.out.println("Initialized repository in " + repo.getRootDir());

        } catch (PlaylessRepositoryException e) {
            e.printStackTrace();
        }
    }
}
