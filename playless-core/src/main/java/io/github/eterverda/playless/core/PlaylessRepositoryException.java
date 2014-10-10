package io.github.eterverda.playless.core;

import io.github.eterverda.playless.common.PlaylessException;

public class PlaylessRepositoryException extends PlaylessException {
    public PlaylessRepositoryException() {
    }

    public PlaylessRepositoryException(String message) {
        super(message);
    }

    public PlaylessRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaylessRepositoryException(Throwable cause) {
        super(cause);
    }
}
