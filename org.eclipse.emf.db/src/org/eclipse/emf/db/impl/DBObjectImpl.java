package org.eclipse.emf.db.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.db.RemoteException;
import org.eclipse.emf.db.util.DBModelInformationCache;
import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.db.util.DBUtil;
import org.eclipse.emf.db.util.LazyLoadingInformation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public abstract class DBObjectImpl extends EObjectImpl implements DBObject {
    private long cdoID=-1;
    private Connection connection;
    private String cdoResource;
    private long cdoRevision=0;
    private final Map<EStructuralFeature, Object> map;
    private Multimap<EReference, DBObject> detached;
    private final EReference containmentRef;
    private boolean isModified=true;

    // private Set<EStructuralFeature> modified=Sets.newHashSet(eClass().getEAllStructuralFeatures());

    protected DBObjectImpl() {
        map=Maps.newHashMapWithExpectedSize(eClass().getEAllStructuralFeatures().size());
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
    public void cdoSetResource(String cdoResource) {
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
                        value=queryAll(((EReference) eFeature), eFeature);
                        value=new DBList((EReference) eFeature, this, (List<DBObject>) value);
                        internalESet(eFeature, value);
                    }
                    return value;
                } else {
                    Object value=map().get(eFeature);

                    // Replace value in the map on the fly
                    if (value instanceof LazyLoadingInformation) {
                        if (connection == null) {
                            System.err.println("CONNECTION = NULL - Demander à Christophe => return null;");
                            return null;
                        }
                        // if (DBModelInformationCache.hasInheritance((EReference) eFeature)) {
                        internalESet(eFeature, value=query((LazyLoadingInformation) value));
                        // if (eFeature.getName().equals("posologie") && value == null)
                        // System.out.println("DBObjectImpl.eGet()");
                        // } else {
                        // if (connection == null) {
                        // System.err.println("CONNECTION = NULL - Demander à Christophe => return null;");
                        // return null;
                        // } else {
                        // internalESet(eFeature, value=query((LazyLoadingInformation) value));
                        // // internalESet(
                        // // eFeature,
                        // // value=DBUtil.query(connection, (LazyLoadingInformation) value, (Class<DBObject>) eFeature.getEType().getInstanceClass(),
                        // // eClass()
                        // // .getEPackage()));
                        // }
                        // }
                    } else if (value == null && ((EReference) eFeature).isContainment() && ((EReference) eFeature).getEOpposite() != null) {
                        DBList values=queryAll(((EReference) eFeature), eFeature);
                        if (values.size() > 1)
                            throw new RemoteException("Found multiple values (instead of 1) for " + ((EReference) eFeature).getEOpposite());

                        internalESet(eFeature, value=(values.isEmpty() ? null : values.get(0)));
                    }

                    return value;
                }
            } else {
                // if (eFeature.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                // String tableName=DBQueryUtil.getTableName((EAttribute) eFeature);
                // if (connection != null) {
                // internalESet(
                // eFeature,
                // new BasicEList<Object>(DBQueryUtil.queryStrings(connection, "SELECT " + CDODBSchema.LIST_VALUE + " FROM " + tableName
                // + " WHERE " + CDODBSchema.LIST_REVISION_ID + '=' + cdoID)));
                // } else {
                // internalESet(eFeature, new BasicEList<Object>());
                // }
                // }
                return map().get(eFeature);
            }
        } catch (SQLException e) {
            throw new RemoteException(e);
        }
    }

    private <T extends DBObject> T query(LazyLoadingInformation lazyLoadingInformation) throws SQLException {
        EPackage pkg=eClass().getEPackage();
        return DBUtil.query(connection, lazyLoadingInformation.getCdoId(),
                (Class<T>) DBModelInformationCache.getEClassFromCdoClass(pkg, lazyLoadingInformation.getClazz()).getInstanceClass(), pkg);
    }

    private DBList queryAll(EReference reference, EStructuralFeature eFeature) throws SQLException {
        String columnName=DBQueryUtil.getColumnNameExt(((EReference) eFeature).getEOpposite());
        DBObjectImpl dbObject=this;
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

        return (EObject) eGet(containmentRef, true);
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
        Object oldValue=eGet(eFeature);
        if (eFeature instanceof EReference) {
            EReference reference=(EReference) eFeature;
            // Handle EOpposite
            EReference opposite=reference.getEOpposite();
            if (opposite != null) {
                if (opposite.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                    // Handle newValue
                    if (newValue == null) {
                        DBObjectImpl obj=(DBObjectImpl) eGet(reference);
                        if (obj != null)
                            ((List<?>) obj.eGet(opposite)).remove(this);
                    } else {
                        List<Object> list=((List<Object>) ((EObject) newValue).eGet(opposite));
                        if (!list.contains(this))
                            list.add(this);
                    }

                    // Handle oldValue
                    // if (oldValue != null) {
                    // if (oldValue instanceof Long) {
                    // DBObject obj=DBUtil.getCache().getIfPresent(oldValue);
                    // if (obj != null)
                    // ((List<Object>) ((EObject) obj).eGet(opposite)).remove(this);
                    // } else
                    // ((List<Object>) ((EObject) oldValue).eGet(opposite)).remove(this);
                    // }
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

            // Handle Detached
            if (newValue == null) {
                if (oldValue != null) {
                    if (oldValue instanceof Long) {
                        DBObject obj=DBUtil.getCache().getIfPresent(oldValue);
                        if (obj != null)
                            dbAddDetached(reference, obj);
                        // System.err.println("Quentin erreur ? pour oldValue=2 avec reference");
                    } else {
                        dbAddDetached(reference, (DBObject) oldValue);
                    }
                }
            } else {
                dbRemoveDetached(reference, (DBObject) newValue);
            }
        }
        internalESet(eFeature, newValue);
        if (!Objects.equals(oldValue, newValue)) {
            eNotify(new ENotificationImpl(this, Notification.SET, eFeature, oldValue, newValue));
            dbSetModified(eFeature);
        }
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

    @Override
    public boolean dbIsModified() {
        return isModified;
    }

    public void dbSetModified(EStructuralFeature feature) {
        isModified=true;
        // if (modified == null)
        // modified=Sets.newHashSet();
        // modified.add(feature);
    }

    public void dbClearModified() {
        isModified=false;
        // modified=null;
    }

    // @Override
    // public Set<EStructuralFeature> dbModified() {
    // if(isModified) {
    //
    // }
    // return modified;
    // }

    public void dbAddDetached(EReference ref, DBObject obj) {
        if (DBUtil.isStoredInMemory(obj))
            return; // NO-OP

        if (detached == null)
            detached=HashMultimap.create();
        detached.put(ref, obj);
    }

    public void dbRemoveDetached(EReference ref, DBObject obj) {
        if (detached != null)
            detached.remove(ref, obj);
    }

    public void dbClearDetached() {
        if (detached != null)
            detached.clear();
    }

    @Override
    public Collection<DBObject> dbDetached(EReference ref) {
        if (detached == null)
            return Collections.emptyList();
        else
            return detached.get(ref);
    }
}
