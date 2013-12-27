package org.eclipse.emf.db.util;

public class DBException extends RuntimeException {
    public DBException() {
    }

    public DBException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBException(String message) {
        super(message);
    }
}
