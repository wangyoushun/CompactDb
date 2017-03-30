package com.six.compactdb.exception;

public class CompactdbException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CompactdbException() {
    }

    public CompactdbException(String message) {
        super(message);
    }

    public CompactdbException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompactdbException(Throwable cause) {
        super(cause);
    }
}
