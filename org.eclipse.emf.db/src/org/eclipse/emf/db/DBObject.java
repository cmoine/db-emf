package org.eclipse.emf.db;

import java.sql.Connection;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

public interface DBObject extends EObject {
    public long cdoID();

    public Connection cdoView();

    public String cdoResource();

    public long cdoRevision();

    public void cdoSetResource(String path);

    /**
     * @return true if this object was modified since the last load from DB. Return always true is the object has not being persisted at all.
     */
    public boolean dbIsModified();

    // public Collection<DBObject> dbDetached(EReference ref);
    public <T> Iterable<T> dbDetached(EReference ref, Class<T> clazz);
}
