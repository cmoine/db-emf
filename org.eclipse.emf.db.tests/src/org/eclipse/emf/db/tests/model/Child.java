/**
 */
package org.eclipse.emf.db.tests.model;

import org.eclipse.emf.db.DBObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Child</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.db.tests.model.Child#getRoot <em>Root</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.db.tests.model.ModelPackage#getChild()
 * @model
 * @extends DBObject
 * @generated
 */
public interface Child extends DBObject {
    /**
     * Returns the value of the '<em><b>Root</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.emf.db.tests.model.Root#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Root</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Root</em>' container reference.
     * @see #setRoot(Root)
     * @see org.eclipse.emf.db.tests.model.ModelPackage#getChild_Root()
     * @see org.eclipse.emf.db.tests.model.Root#getChildren
     * @model opposite="children" transient="false"
     * @generated
     */
    Root getRoot();

    /**
     * Sets the value of the '{@link org.eclipse.emf.db.tests.model.Child#getRoot <em>Root</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Root</em>' container reference.
     * @see #getRoot()
     * @generated
     */
    void setRoot(Root value);

} // Child
