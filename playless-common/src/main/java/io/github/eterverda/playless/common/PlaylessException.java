package io.github.eterverda.playless.common;

public class PlaylessException extends Exception {
    public PlaylessException() {
    }

    public PlaylessException(String message) {
        super(message);
    }

    public PlaylessException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaylessException(Throwable cause) {
        super(cause);
    }
}
