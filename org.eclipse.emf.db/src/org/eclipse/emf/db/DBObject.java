package org.eclipse.emf.db;

import java.sql.Connection;

import org.eclipse.emf.ecore.EObject;

public interface DBObject extends EObject {
    public long cdoID();

    public Connection cdoView();

    public String cdoResource();

    public long cdoRevision();

    public void setResource(String path);
}
