package org.eclipse.emf.db;

import java.sql.Connection;
import java.util.Collection;

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

    // /**
    // * @return The list of structural feature modified if any, may return null if none
    // */
    // public Set<EStructuralFeature> dbModified();

    public Collection<DBObject> dbDetached(EReference ref);
}
