package io.github.eterverda.playless.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Argument;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.internal.HelpScreenException;

import java.io.File;

import io.github.eterverda.playless.core.PlaylessRepositoryException;

public final class Main {

    public static final String COMMAND = "command";

    public static void main(final String... args) throws PlaylessRepositoryException, ArgumentParserException {
        final ArgumentParser parser = ArgumentParsers.newArgumentParser("playless");
        parser.addSubparsers().title(COMMAND).metavar(COMMAND);

        DumpCommand.addSubParser(parser).setDefault(COMMAND, DumpCommand.class);

        InstallCommand.addSubParser(parser).setDefault(COMMAND, InstallCommand.class);

        InitCommand.addSubParser(parser).setDefault(COMMAND, InitCommand.class);

        try {
            main(parser.parseArgs(args));

        } catch (HelpScreenException ex) {
            System.exit(0);

        } catch (ArgumentParserException ex) {
            System.err.println(ex.getMessage());
            System.err.println();
            parser.printUsage();
            System.exit(1);
        }
    }

    private static void main(Namespace args) {
        try {
            final Class<Command> cls = args.get(COMMAND);
            final Command command = cls.newInstance();
            command.main(args);

        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    public static Argument addRepoArgument(Subparser parser) {
        return parser.addArgument("-r", "--repo")
                .type(Arguments.fileType()).setDefault(new File("."))
                .help("path to repository (defaults to working dir)");
    }

    public static Argument addApksArgument(Subparser parser) {
        return parser.addArgument("apk").nargs("+")
                .type(Arguments.fileType());
    }
}
