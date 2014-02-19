package org.eclipse.emf.db.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import com.google.common.collect.Iterables;

public abstract class DBRunnable {

    /* package */Connection connection;

    public abstract void run() throws SQLException;

    public void save(DBObject obj, EStructuralFeature... features) throws SQLException {
        doSave(obj, Arrays.asList(features));
    }

    public void save(DBObject obj) throws SQLException {
        doSave(obj, obj.eClass().getEAllStructuralFeatures());
    }

    @SuppressWarnings("unchecked")
    public void save(DBObject obj, Collection<? extends EStructuralFeature> features) throws SQLException {
        doSave(obj, (Collection<EStructuralFeature>) features);
    }

    private void doSave(final DBObject obj, Collection<EStructuralFeature> features) throws SQLException {
        DBUtil.doSave(connection, obj, features);
    }

    public void delete(DBObject obj) throws SQLException {
        DBUtil.delete(connection, obj);
    }

    public void rawExecute(String query) throws SQLException {
        Statement stmt=connection.createStatement();
        try {
            stmt.execute(query);
        } finally {
            stmt.close();
        }
    }

    public void reload(DBObject obj) throws SQLException {
        DBUtil.reload(connection, obj);
    }

    public void saveDeep(DBObject dbObject) throws SQLException {
        saveDeep(dbObject, -1);
    }

    /**
     * @param dbObject
     *            the {@link DBObject} to save
     * @param depth
     *            -1 : unlimited<br>
     *            0 : this object only<br>
     *            1..n : this object and n level of children
     * 
     * @throws SQLException
     */
    public void saveDeep(DBObject dbObject, int deep) throws SQLException {
        if (dbObject == null)
            return;
        // QLE deep < 0 pour deep=-1 & deep >=1 si on appel la methode avec un deep > à 1
        if (dbObject.dbIsModified())
            save(dbObject);

        if (deep == 0)
            return;

        for (EReference ref : dbObject.eClass().getEAllContainments()) {
            for (DBObject childDBObject : Iterables.filter((List<Object>) dbObject.eGet(ref), DBObject.class))
                saveDeep(childDBObject, deep - 1);

            for (DBObject childDBObject : dbObject.dbDetached(ref))
                delete(childDBObject);
        }
    }
}
