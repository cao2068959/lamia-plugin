package org.chy.lamiaplugin.exception;

public class LamiaConvertException extends RuntimeException{
    public LamiaConvertException() {
    }

    public LamiaConvertException(String message) {
        super(message);
    }

    public LamiaConvertException(String message, Throwable cause) {
        super(message, cause);
    }
}
