package org.eclipse.emf.db.impl;

import java.util.Collection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;

/* package */class DBList extends BasicEList<DBObject> {
    private final EReference opposite;
    private final DBObjectImpl owner;
    private final EReference ref;

    public DBList(EReference ref, DBObjectImpl owner) {
        this.ref=ref;
        this.owner=owner;
        opposite=getOpposite(ref);
    }

    public DBList(EReference ref, DBObjectImpl owner, Collection<? extends DBObject> collection) {
        super(collection);
        this.ref=ref;
        this.owner=owner;
        opposite=getOpposite(ref);
    }

    public DBList(EReference ref, DBObjectImpl owner, int initialCapacity) {
        super(initialCapacity);
        this.ref=ref;
        this.owner=owner;
        opposite=getOpposite(ref);
    }

    // QLE : Voir le test TestDBEMF.addElementToContainer
    @Override
    protected boolean isUnique() {
        return true;
    }

    private static EReference getOpposite(EReference ref) {
        if (ref.getEOpposite() != null) {
            EReference result=ref.getEOpposite();
            if (result.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY)
                throw new UnsupportedOperationException("Does not support 0..* - 0..* relations"); //$NON-NLS-1$
            return result;
        } else
            return null;
    }

    @Override
    public DBObject set(int index, DBObject object) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void didAdd(int index, DBObject newObject) {
        if (opposite != null) {
            DBObject oldOwner=(DBObject) newObject.eGet(opposite);
            if (oldOwner != null)
                ((DBList) oldOwner.eGet(ref)).remove(newObject);

            ((DBObjectImpl) newObject).internalESet(opposite, owner);
            ((DBObjectImpl) newObject).dbSetModified(opposite);
            owner.dbRemoveDetached(ref, newObject);
        }
    }

    @Override
    protected void didRemove(int index, DBObject oldObject) {
        if (opposite != null) {
            ((DBObjectImpl) oldObject).internalESet(opposite, null);
            ((DBObjectImpl) oldObject).dbSetModified(opposite);
            owner.dbAddDetached(ref, oldObject);
        }
    }

}
