package net.lbku.exception;

public class TwitterServiceException extends RuntimeException {
    public TwitterServiceException(String message) {
        super(message);
    }

    public TwitterServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
