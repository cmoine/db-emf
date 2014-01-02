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

    public void setResource(String path);

    public boolean isModified();

    public Collection<DBObject> detached(EReference ref);
}
