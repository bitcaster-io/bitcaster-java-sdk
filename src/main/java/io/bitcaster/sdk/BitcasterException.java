package io.bitcaster.sdk;

public class BitcasterException extends RuntimeException {
    public BitcasterException(String message) {
        super(message);
    }

    public BitcasterException(String message, Throwable cause) {
        super(message, cause);
    }
}