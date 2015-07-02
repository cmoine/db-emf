package org.eclipse.emf.db.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
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

import com.google.common.base.Objects;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public abstract class DBObjectImpl extends EObjectImpl implements DBObject {
    private long cdoID=-1;
    private Connection connection;
    private String cdoResource;
    private long cdoRevision=0;
    private final Map<EStructuralFeature, Object> map;
    private Map<EStructuralFeature, Object> ori;
    private Multimap<EReference, DBObject> detached;
    private final EReference containmentRef;

    protected class DBPropertiesHolderImpl extends EPropertiesHolderBaseImpl {
        @Override
        public URI getEProxyURI() {
            return null;
        }

        @Override
        public boolean hasSettings() {
            return true;
        }

        @Override
        public void allocateSettings(int dynamicFeatureCount) {
            // NO-OP
        }

        @Override
        public Object dynamicGet(int dynamicFeatureID) {
            return eGet(eClass.getEStructuralFeature(dynamicFeatureID));
        }

        @Override
        public void dynamicSet(int dynamicFeatureID, Object value) {
            eSet(eClass.getEStructuralFeature(dynamicFeatureID), value);
        }

        @Override
        public void dynamicUnset(int dynamicFeatureID) {
            throw new UnsupportedOperationException();
        }
    }
    // private boolean isModified=true;

    protected DBObjectImpl() {
        map=Maps.newHashMapWithExpectedSize(eClass().getEAllStructuralFeatures().size());
        containmentRef=DBModelInformationCache.getContainerReference(eClass());
        // FIXME Workaround set default values to prevent NPE :(
        for (EAttribute att : eClass().getEAllAttributes()) {
            internalESet(att, att.getDefaultValue());
        }
        eSetClass(eStaticClass());
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
    protected EPropertiesHolder eProperties() {
        if (eProperties == null) {
            eProperties=new DBPropertiesHolderImpl();
        }
        return eProperties;
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
                        // FIXME c'est degueu
                        value=queryAll((EReference) eFeature, eFeature);
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
                        internalESet(eFeature, value=query((LazyLoadingInformation) value));
                    } else if (value == null && ((EReference) eFeature).isContainment() && ((EReference) eFeature).getEOpposite() != null) {
                        DBList values=queryAll((EReference) eFeature, eFeature);
                        if (values.size() > 1)
                            throw new RemoteException("Found multiple values (instead of 1) for " + ((EReference) eFeature).getEOpposite());

                        internalESet(eFeature, value=values.isEmpty() ? null : values.get(0));
                    }

                    return value;
                }
            } else {
                // EAttribute
                Object value=map().get(eFeature);
                if (value == LazyValue.INSTANCE) {
                    loadLazyValues();
                    value=map().get(eFeature);
                }
                // Workaround for null values
                if (value == null && eFeature.getEType().getInstanceClass().isPrimitive())
                    return eFeature.getDefaultValue();

                return value;
            }
        } catch (SQLException e) {
            throw new RemoteException(e);
        }
    }

    private void loadLazyValues() throws SQLException {
        // Clear all lazy values
        for (Iterator<Entry<EStructuralFeature, Object>> iterator=map.entrySet().iterator(); iterator.hasNext();) {
            Entry<EStructuralFeature, Object> entry=iterator.next();
            if (entry.getValue() == LazyValue.INSTANCE) {
                iterator.remove();
            }
        }
        DBUtil.reload(connection, this);
    }

    private <T extends DBObject> T query(LazyLoadingInformation lazyLoadingInformation) throws SQLException {
        EPackage pkg=eClass().getEPackage();
        if (lazyLoadingInformation.getClazz() == 0){
            System.err.println("cdo_container_internal_class is NULL (id="+lazyLoadingInformation.getCdoId()+", eclass="+eClass().getName()+")");
            return null;
        }
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
        Assert.isTrue(eFeature.getEContainingClass().isSuperTypeOf(eClass()), //
                eFeature.getEContainingClass().getName() + " must be a superclass of " + eClass().getName()); //$NON-NLS-1$
        if (map().get(eFeature) == LazyValue.INSTANCE) {
            // No notifications
            internalESet(eFeature, newValue);
        } else {
            // Notify
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
                            List<Object> list=(List<Object>) ((EObject) newValue).eGet(opposite);
                            if (!list.contains(this))
                                list.add(this);
                        }

                        // TODO Handle oldValue
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
                        if (oldValue instanceof LazyLoadingInformation) {
                            try {
                                DBObject obj=query((LazyLoadingInformation) oldValue);
                                if (obj != null)
                                    dbAddDetached(reference, obj);
                            } catch (SQLException e) {
                                throw new RemoteException(e);
                            }
                        } else {
                            dbAddDetached(reference, (DBObject) oldValue);
                        }
                    }
                } else {
                    dbRemoveDetached(reference, (DBObject) newValue);
                }
            }
            internalESet(eFeature, newValue);
            if (!Objects.equal(oldValue, newValue)) {
                eNotify(new ENotificationImpl(this, Notification.SET, eFeature, oldValue, newValue));
                dbSetModified(eFeature, oldValue);
            }
        }
    }

    private static class LazyValue {
        public static final LazyValue INSTANCE=new LazyValue();

        private LazyValue() {
        }
    }

    public void internalSetLazy(EAttribute eFeature) {
        internalESet(eFeature, LazyValue.INSTANCE);
    }

    public void internalESet(EStructuralFeature eFeature, Object newValue) {
        map().put(eFeature, newValue);
    }

    public Map<EStructuralFeature, Object> map() {
        return map;
    }

    public Map<EStructuralFeature, Object> ori() {
        return ori;
    }

    @Override
    protected final Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    // @Override
    // public boolean dbIsModified() {
    // return isModified;
    // }

    @Override
    public boolean dbIsModified() {
        return ori != null && !ori.isEmpty();
    }

    public void dbSetModified(EStructuralFeature feature, Object oldValue) {
        if (ori == null || !ori.containsKey(feature)) {
            ori=Maps.newHashMapWithExpectedSize(eClass().getEAllStructuralFeatures().size());
            ori.put(feature, oldValue);
        }
    }

    public void dbClearModified() {
        if (ori != null)
            ori.clear();
    }

    //
    // public void dbSetModified(EStructuralFeature feature) {
    // isModified=true;
    // }
    //
    // public void dbClearModified() {
    // isModified=false;
    // }

    public void dbAddDetached(EReference ref, DBObject obj) {
        if (DBUtil.isStoredInMemory(obj))
            return; // NO-OP

        dbForceAddDetached(ref, obj);
    }

    public void dbForceAddDetached(EReference ref, DBObject obj) {
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

    // @Override
    // public Collection<DBObject> dbDetached(EReference ref) {
    // if (detached == null)
    // return Collections.emptyList();
    // else
    // return detached.get(ref);
    // }

    @Override
    public String toString() {
        StringBuffer buf=new StringBuffer(eClass().getName()).append('@').append(Integer.toHexString(hashCode()));
        buf.append("{cdoId:").append(cdoID()).append(','); //$NON-NLS-1$
        for (org.eclipse.emf.ecore.EStructuralFeature feature : eClass().getEAllStructuralFeatures()) {
            buf.append(feature.getName()).append(':');
            Object eGet=eGet(feature);
            if (feature instanceof EAttribute) {
                buf.append(eGet);
            } else if (feature instanceof EReference && eGet != null && feature.getUpperBound() == 1) {
                DBObject obj=(DBObject) eGet;
                buf.append(obj.eClass().getName()).append(':').append(obj.cdoID());
            }
            buf.append(',');
        }
        buf.append('}');
        return buf.toString();
    }

    @Override
    public <T> Iterable<T> dbDetached(EReference ref, Class<T> clazz) {
        Assert.isTrue(ref.getEType() instanceof EClass, "Reference type must be an eClass"); //$NON-NLS-1$
        Class<?> instanceClass=((EClass) ref.getEType()).getInstanceClass();
        Assert.isTrue(clazz.isAssignableFrom(instanceClass), "Class " + instanceClass + " is not assignable from " + clazz); //$NON-NLS-1$ //$NON-NLS-2$
        if (detached == null)
            return Collections.emptyList();
        else
            return Iterables.filter(detached.get(ref), clazz);
    }

    @Override
    public EList<EObject> eContents() {
        // For Quentine to respect EMF contract
        EList<EObject> result=new BasicEList<EObject>();
        for (EReference ref : eClass().getEAllContainments()) {
            if (ref.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                result.addAll((Collection<? extends EObject>) eGet(ref));
            }
        }
        return result;
    }
}
