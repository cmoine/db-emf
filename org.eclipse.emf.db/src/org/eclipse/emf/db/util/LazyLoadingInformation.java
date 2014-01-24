package org.eclipse.emf.db.util;


public class LazyLoadingInformation {
    private final long cdoId;
    private final int clazz;

    LazyLoadingInformation(long cdoId, int clazz) {
        this.cdoId=cdoId;
        this.clazz=clazz;
    }

    public long getCdoId() {
        return cdoId;
    }

    public int getClazz() {
        return clazz;
    }
}
