package org.eclipse.emf.db;

public class RemoteException extends RuntimeException {
    private static final long serialVersionUID=5009558569846457975L;

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message) {
        super(message);
    }
}
