package io.github.eterverda.playless.core;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public final class Repository {
    @NotNull
    private final File rootDir;

    public Repository(@Nullable File dir) throws PlaylessRepositoryException {
        final File normalDir = normalDir(dir);
        final File rootDir = repoRootDir(normalDir);
        if (rootDir == null) {
            throw new PlaylessRepositoryException("No repository found in " + normalDir);
        }
        this.rootDir = rootDir;
    }

    private Repository(@NotNull File rootDir, @SuppressWarnings("unused") boolean ok) {
        this.rootDir = rootDir;
    }

    @NotNull
    public File getRootDir() {
        return rootDir;
    }

    @NotNull
    private static File normalDir(@Nullable File dir) throws PlaylessRepositoryException {
        try {
            final File providedDir = dir != null ? dir : new File(".");
            return providedDir.getCanonicalFile();
        } catch (IOException e) {
            throw new PlaylessRepositoryException(e);
        }
    }

    @Nullable
    private static File repoRootDir(@NotNull File dir) {
        final File markerFile = markerFile(dir);
        if (markerFile.exists() && markerFile.isFile()) {
            return dir;
        }
        final File parentDir = dir.getParentFile();
        if (parentDir == null) {
            return null;
        }
        return repoRootDir(parentDir);
    }

    private static File markerFile(File dir) {
        final File internalsDir = internalsDir(dir);
        return new File(internalsDir, "root");
    }

    private static File internalsDir(File dir) {
        return new File(dir, ".playless");
    }

    @NotNull
    public static Repository init(@Nullable File dir) throws PlaylessRepositoryException {
        final File normalDir = normalDir(dir);
        final File rootDir = repoRootDir(normalDir);
        if (rootDir != null) {
            if (!normalDir.equals(rootDir)) {
                throw new PlaylessRepositoryException("Repository already exists in " + rootDir);
            }

        } else {
            final File internalsDir = internalsDir(normalDir);
            if (internalsDir.exists() && !internalsDir.isDirectory()) {
                throw new PlaylessRepositoryException("File exists " + internalsDir + " and not a directory");
            }
            if (!internalsDir.mkdirs()) {
                throw new PlaylessRepositoryException(new IOException("Cannot create directory " + internalsDir));
            }
            final File markerFile = markerFile(normalDir);
            if (markerFile.exists() && !markerFile.isFile()) {
                throw new PlaylessRepositoryException("File exists " + markerFile + " and is a a directory");
            }

            try {
                new FileOutputStream(markerFile).close();
            } catch (IOException e) {
                throw new PlaylessRepositoryException("Cannot create file " + markerFile, e);
            }
        }
        return new Repository(normalDir, true);
    }
}
