package org.chy.lamiaplugin.exception;

public class NoSuchMethodException extends RuntimeException {

    /**
     * Constructs a {@code NoSuchMethodException} without a detail message.
     */
    public NoSuchMethodException() {
        super();
    }

    /**
     * Constructs a {@code NoSuchMethodException} with a detail message.
     *
     * @param      s   the detail message.
     */
    public NoSuchMethodException(String s) {
        super(s);
    }
}
