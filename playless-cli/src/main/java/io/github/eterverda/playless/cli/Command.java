package io.github.eterverda.playless.cli;

import net.sourceforge.argparse4j.inf.Namespace;

public interface Command {
    void main(Namespace args);
}
