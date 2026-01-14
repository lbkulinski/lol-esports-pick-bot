package net.lbku.mediawiki.exception;

public final class MediaWikiException extends RuntimeException {
    public MediaWikiException(String message) {
        super(message);
    }

    public MediaWikiException(String message, Throwable cause) {
        super(message, cause);
    }
}
