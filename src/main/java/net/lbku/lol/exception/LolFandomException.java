package net.lbku.lol.exception;

public final class LolFandomException extends RuntimeException {
    public LolFandomException(String message) {
        super(message);
    }

    public LolFandomException(String message, Throwable cause) {
        super(message, cause);
    }
}
