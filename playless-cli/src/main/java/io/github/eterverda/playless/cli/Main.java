package io.github.eterverda.playless.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import io.github.eterverda.playless.core.PlaylessRepositoryException;
import io.github.eterverda.playless.core.Repository;

public final class Main {
    private final static Map<String, Command> commands;

    static {
        commands = new HashMap<>();
        commands.put("init", new InitCommand());
        commands.put("dump", new DumpCommand());
    }

    public static void main(final String... rawArgs) throws PlaylessRepositoryException {
        final ArrayList<String> args = new ArrayList<>(rawArgs.length);
        Collections.addAll(args, rawArgs);

        main(args);
    }

    private static void main(ArrayList<String> args) throws PlaylessRepositoryException {
        try {
            final String cmd = extractCommand(args);
            final Command command = commands.get(cmd);

            if (command == null) {
                throw new IllegalArgumentException("Unknown command " + cmd);
            }

            final String repoPath = extractRepoPath(args);
            final File repoDir = repoPath != null ? new File(repoPath) : null;
            final Repository repo = command instanceof InitCommand ?
                    Repository.init(repoDir) :
                    new Repository(repoDir);

            final String[] commandArgs = args.toArray(new String[args.size()]);

            command.main(repo, commandArgs);

        } catch (PlaylessRepositoryException ex) {
            System.err.println("Error: " + ex.getMessage());

        } catch (IllegalArgumentException ex) {
            System.err.println("Usage: init [-r <path_to_repo>]");
            System.err.println("       dump [--pretty] --aapt <path_to_aapt> <apk_file>...");
        }
    }

    private static String extractCommand(ArrayList<String> args) {
        if (args.size() == 0) {
            throw new IllegalArgumentException("No subcommand");
        }
        return args.remove(0);
    }

    private static String extractRepoPath(ArrayList<String> args) {
        for (int i = 0; i < args.size(); i++) {
            final String arg = args.get(i);
            if (arg.startsWith("-r=")) {
                args.remove(i);
                return arg.substring(3);
            }
            if (arg.startsWith("--repository=")) {
                args.remove(i);
                return arg.substring(13);
            }
            if (arg.equals("-r") || arg.equals("--repository")) {
                if (args.size() == i - 1) {
                    throw new IllegalArgumentException("No value for option " + arg);
                }
                args.remove(i);
                return args.remove(i);
            }
        }
        return null;
    }
}
