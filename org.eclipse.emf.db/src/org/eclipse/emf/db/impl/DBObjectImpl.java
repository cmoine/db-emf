package org.eclipse.emf.db.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.db.DBModelInformationCache;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.db.RemoteException;
import org.eclipse.emf.db.util.DBList;
import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.db.util.DBUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.google.common.collect.Maps;

public abstract class DBObjectImpl extends EObjectImpl implements DBObject {
    private long cdoID=-1;
    private Connection connection;
    private String cdoResource;
    private long cdoRevision=0;
    private final Map<EStructuralFeature, Object> map=Maps.newHashMap();
    private final EReference containmentRef;

    protected DBObjectImpl() {
        containmentRef=DBModelInformationCache.getContainerReference(eClass());
        // FIXME Workaround set default values to prevent NPE :(
        for (EAttribute att : eClass().getEAllAttributes()) {
            internalESet(att, att.getDefaultValue());
        }
    }

    @Override
    public long cdoID() {
        return cdoID;
    }

    public void setCdoID(long val) {
        cdoID=val;
    }

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public Connection cdoView() {
        return connection;
    }

    @Override
    public String cdoResource() {
        return cdoResource;
    }

    @Override
    public long cdoRevision() {
        return cdoRevision;
    }

    public void setRevision(long cdoRevision) {
        this.cdoRevision=cdoRevision;
    }

    @Override
    public void setResource(String cdoResource) {
        this.cdoResource=cdoResource;
    }

    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve, boolean coreType) {
        return eGet(eFeature, resolve);
    }

    @Override
    public Object eGet(EStructuralFeature eFeature, boolean resolve) {
        try {
            if (eFeature instanceof EReference) {
                if (eFeature.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                    Object value=map().get(eFeature);
                    if (value == null) {
                        if (((EReference) eFeature).isContainment()) {
                            value=queryAll(this, ((EReference) eFeature), CDODBSchema.ATTRIBUTES_CONTAINER);
                        } else {
                            value=queryAll(this, ((EReference) eFeature), DBQueryUtil.getColumnName(((EReference) eFeature).getEOpposite()));
                        }
                        value=new DBList((EReference) eFeature, this, (List<DBObject>) value);
                        internalESet(eFeature, value);
                    }
                    return value;
                } else {
                    Object value=map().get(eFeature);

                    // Replace value in the map on the fly
                    if (value instanceof Long) {
                        if (DBModelInformationCache.hasInheritance((EReference) eFeature)) {
                            internalESet(eFeature, value=query(this, (EReference) eFeature));
                        } else {
                            if (connection == null) {
                                System.err.println("CONNECTION = NULL - Demander à Christophe => return null;");
                                return null;
                            } else {
                                internalESet(
                                        eFeature,
                                        value=DBUtil.query(connection, (Long) value, (Class<DBObject>) eFeature.getEType().getInstanceClass(), eClass()
                                                .getEPackage()));
                            }
                        }
                    }
                    return value;
                }
            } else {
                if (eFeature.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                    String tableName=DBQueryUtil.getTableName((EAttribute) eFeature);
                    if (connection != null) {
                        internalESet(
                                eFeature,
                                new BasicEList<Object>(DBQueryUtil.queryStrings(connection, "SELECT " + CDODBSchema.LIST_VALUE + " FROM " + tableName
                                        + " WHERE " + CDODBSchema.LIST_REVISION_ID + '=' + cdoID)));
                    } else {
                        internalESet(eFeature, new BasicEList<Object>());
                    }
                }
                return map().get(eFeature);
            }
        } catch (SQLException e) {
            throw new RemoteException(e);
        }
    }

    private <T extends DBObject> T query(DBObject dbObject, EReference reference) throws SQLException {
        DBObjectImpl dbObjectImpl=(DBObjectImpl) dbObject;
        long cdoID=(Long) dbObjectImpl.map().get(reference);
        // On se fatigue, on cherche la bonne classe et on delegue
        EClass eClass=(EClass) reference.getEType();
        if (DBModelInformationCache.hasInheritance(reference)) {
            int cdoInternalClass=Integer.MIN_VALUE;
            Statement stmt=((DBObjectImpl) dbObject).connection.createStatement();
            stmt.closeOnCompletion();
            ResultSet rSet=stmt.executeQuery("SELECT " + DBUtil.internalClass(reference) + " FROM " //$NON-NLS-1$ //$NON-NLS-2$
                    + DBQueryUtil.getTableName(dbObject.eClass()) + " WHERE " + CDODBSchema.ATTRIBUTES_ID + '=' + dbObject.cdoID()); //$NON-NLS-1$
                if (rSet.next()) {
                    cdoInternalClass=rSet.getInt(1);
                }
            eClass=DBModelInformationCache.getEClassFromCdoClass(dbObject.eClass().getEPackage(), cdoInternalClass);
            if (eClass == null)
                return null;
        }
        return DBUtil.query(connection, cdoID, (Class<T>) eClass.getInstanceClass(), eClass().getEPackage());
    }

    private DBList queryAll(DBObject dbObject, EReference reference, String columnName) throws SQLException {
        if (dbObject.cdoID() == -1) {
            return new DBList(reference, dbObject);
        }

        EClass eClass=(EClass) reference.getEType();
        long containerID=dbObject.cdoID();
        String query="SELECT * FROM " + DBQueryUtil.getTableName(eClass) //$NON-NLS-1$
                + " WHERE " + columnName + '=' + containerID; //$NON-NLS-1$

        if (DBModelInformationCache.hasInheritance(reference.getEOpposite())) {
            query=query + " AND " + DBUtil.internalClass(reference.getEOpposite()) //$NON-NLS-1$
                    + '=' + DBUtil.cdoInternalClass(dbObject.eClass());
        }
        return new DBList(reference, dbObject, DBUtil.getAll(connection, (Class<DBObject>) eClass.getInstanceClass(), eClass().getEPackage(), query));
    }

    public void setConnection(Connection connection) {
        this.connection=connection;
    }

    @Override
    public EObject eContainer() {
        if (containmentRef == null) {
            return null;
        }

        return (EObject) eGet(containmentRef, true, true);
    }

    @Override
    public EReference eContainmentFeature() {
        return containmentRef;
    }

    @Override
    public boolean eIsSet(EStructuralFeature eFeature) {
        Object value=map().get(eFeature);
        if (value == null)
            return false;

        if (eFeature instanceof EReference && value instanceof Long)
            return false;

        return !value.equals(eFeature.getDefaultValue());
    }

    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue) {
        Object oldValue=map().get(eFeature);
        if (eFeature instanceof EReference) {
            EReference reference=((EReference) eFeature);
            EReference opposite=reference.getEOpposite();
            if (opposite != null) {
                if (opposite.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                    if (newValue == null) {
                        DBObjectImpl obj=(DBObjectImpl) eGet(reference);
                        if (obj != null)
                            ((List<?>) obj.eGet(opposite)).remove(this);
                    } else {
                        List<Object> list=((List<Object>) ((EObject) newValue).eGet(opposite));
                        if (!list.contains(this))
                            list.add(this);
                    }
                } else {
                    if (newValue == null) {
                        DBObjectImpl obj=(DBObjectImpl) eGet(reference);
                        if (obj != null)
                            obj.internalESet(opposite, null);
                    } else {
                        ((DBObjectImpl) newValue).internalESet(opposite, this);
                    }
                }
            }
        }
        internalESet(eFeature, newValue);
        eNotify(new ENotificationImpl(this, Notification.SET, eFeature, oldValue, newValue));
    }

    public void internalESet(EStructuralFeature eFeature, Object newValue) {
        map().put(eFeature, newValue);
    }

    public Map<EStructuralFeature, Object> map() {
        return map;
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
