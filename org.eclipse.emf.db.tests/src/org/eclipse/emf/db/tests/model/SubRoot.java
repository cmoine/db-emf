/**
 */
package org.eclipse.emf.db.tests.model;

import org.eclipse.emf.db.DBObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Sub Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.db.tests.model.SubRoot#getRoot <em>Root</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.db.tests.model.ModelPackage#getSubRoot()
 * @model
 * @extends DBObject
 * @generated
 */
public interface SubRoot extends DBObject {
    /**
     * Returns the value of the '<em><b>Root</b></em>' container reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.emf.db.tests.model.Root#getSubRoot <em>Sub Root</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Root</em>' container reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Root</em>' container reference.
     * @see #setRoot(Root)
     * @see org.eclipse.emf.db.tests.model.ModelPackage#getSubRoot_Root()
     * @see org.eclipse.emf.db.tests.model.Root#getSubRoot
     * @model opposite="subRoot" transient="false"
     * @generated
     */
    Root getRoot();

    /**
     * Sets the value of the '{@link org.eclipse.emf.db.tests.model.SubRoot#getRoot <em>Root</em>}' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Root</em>' container reference.
     * @see #getRoot()
     * @generated
     */
    void setRoot(Root value);

} // SubRoot
