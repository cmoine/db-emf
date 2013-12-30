/**
 */
package org.eclipse.emf.db.tests.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.db.tests.model.ModelPackage
 * @generated
 */
public interface ModelFactory extends EFactory {
    /**
     * The singleton instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelFactory eINSTANCE = org.eclipse.emf.db.tests.model.impl.ModelFactoryImpl.init();

    /**
     * Returns a new object of class '<em>Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Root</em>'.
     * @generated
     */
    Root createRoot();

    /**
     * Returns a new object of class '<em>Child</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Child</em>'.
     * @generated
     */
    Child createChild();

    /**
     * Returns a new object of class '<em>Sub Root</em>'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return a new object of class '<em>Sub Root</em>'.
     * @generated
     */
    SubRoot createSubRoot();

    /**
     * Returns the package supported by this factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the package supported by this factory.
     * @generated
     */
    ModelPackage getModelPackage();

} //ModelFactory
