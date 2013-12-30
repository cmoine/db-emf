/**
 */
package org.eclipse.emf.db.tests.model.impl;

import org.eclipse.emf.db.impl.DBObjectImpl;

import org.eclipse.emf.db.tests.model.ModelPackage;
import org.eclipse.emf.db.tests.model.Root;
import org.eclipse.emf.db.tests.model.SubRoot;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Sub Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.db.tests.model.impl.SubRootImpl#getRoot <em>Root</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class SubRootImpl extends DBObjectImpl implements SubRoot {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected SubRootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.SUB_ROOT;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected int eStaticFeatureCount() {
        return 0;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Root getRoot() {
        return (Root)eGet(ModelPackage.Literals.SUB_ROOT__ROOT, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setRoot(Root newRoot) {
        eSet(ModelPackage.Literals.SUB_ROOT__ROOT, newRoot);
    }

} //SubRootImpl
