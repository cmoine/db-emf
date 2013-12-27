package org.eclipse.emf.db.util;

import java.util.Collection;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.db.impl.DBObjectImpl;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypedElement;

public class DBList extends BasicEList<DBObject> {
    private final EReference opposite;
    private final DBObject owner;

    public DBList(EReference ref, DBObject owner) {
        this.owner=owner;
        opposite=getOpposite(ref);
    }

    public DBList(EReference ref, DBObject owner, Collection<? extends DBObject> collection) {
        super(collection);
        this.owner=owner;
        opposite=getOpposite(ref);
    }

    public DBList(EReference ref, DBObject owner, int initialCapacity) {
        super(initialCapacity);
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
            ((DBObjectImpl) newObject).internalESet(opposite, owner);
        }
    }

    @Override
    protected void didRemove(int index, DBObject oldObject) {
        if (opposite != null) {
            ((DBObjectImpl) oldObject).internalESet(opposite, null);
        }
    }

}
