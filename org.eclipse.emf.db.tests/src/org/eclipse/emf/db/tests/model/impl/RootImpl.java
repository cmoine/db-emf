/**
 */
package org.eclipse.emf.db.tests.model.impl;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.db.impl.DBObjectImpl;

import org.eclipse.emf.db.tests.model.Child;
import org.eclipse.emf.db.tests.model.ModelPackage;
import org.eclipse.emf.db.tests.model.Root;
import org.eclipse.emf.db.tests.model.SubRoot;

import org.eclipse.emf.ecore.EClass;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.emf.db.tests.model.impl.RootImpl#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.db.tests.model.impl.RootImpl#getSubRoot <em>Sub Root</em>}</li>
 *   <li>{@link org.eclipse.emf.db.tests.model.impl.RootImpl#getLabels <em>Labels</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class RootImpl extends DBObjectImpl implements Root {
    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    protected RootImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    protected EClass eStaticClass() {
        return ModelPackage.Literals.ROOT;
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
    @SuppressWarnings("unchecked")
    public EList<Child> getChildren() {
        return (EList<Child>)eGet(ModelPackage.Literals.ROOT__CHILDREN, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SubRoot getSubRoot() {
        return (SubRoot)eGet(ModelPackage.Literals.ROOT__SUB_ROOT, true);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public void setSubRoot(SubRoot newSubRoot) {
        eSet(ModelPackage.Literals.ROOT__SUB_ROOT, newSubRoot);
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @SuppressWarnings("unchecked")
    public EList<String> getLabels() {
        return (EList<String>)eGet(ModelPackage.Literals.ROOT__LABELS, true);
    }

} //RootImpl
