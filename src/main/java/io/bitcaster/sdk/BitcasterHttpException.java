package io.bitcaster.sdk;

public class BitcasterHttpException extends BitcasterException {
    private final int statusCode;

    public BitcasterHttpException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}