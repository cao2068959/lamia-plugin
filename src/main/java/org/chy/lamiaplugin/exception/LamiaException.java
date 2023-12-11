package org.chy.lamiaplugin.exception;

public class LamiaException extends RuntimeException{
    public LamiaException() {
    }

    public LamiaException(String message) {
        super(message);
    }

    public LamiaException(String message, Throwable cause) {
        super(message, cause);
    }
}
