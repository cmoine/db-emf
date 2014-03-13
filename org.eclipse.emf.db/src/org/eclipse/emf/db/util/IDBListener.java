package org.eclipse.emf.db.util;

import org.eclipse.emf.db.DBObject;

public interface IDBListener {
    public void created(DBObject object);

    public void modified(DBObject object);

    public void deleted(DBObject object);
}
