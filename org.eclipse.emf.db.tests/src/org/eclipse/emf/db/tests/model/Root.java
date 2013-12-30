/**
 */
package org.eclipse.emf.db.tests.model;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.db.DBObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Root</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.emf.db.tests.model.Root#getChildren <em>Children</em>}</li>
 *   <li>{@link org.eclipse.emf.db.tests.model.Root#getSubRoot <em>Sub Root</em>}</li>
 *   <li>{@link org.eclipse.emf.db.tests.model.Root#getLabels <em>Labels</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.emf.db.tests.model.ModelPackage#getRoot()
 * @model
 * @extends DBObject
 * @generated
 */
public interface Root extends DBObject {
    /**
     * Returns the value of the '<em><b>Children</b></em>' containment reference list.
     * The list contents are of type {@link org.eclipse.emf.db.tests.model.Child}.
     * It is bidirectional and its opposite is '{@link org.eclipse.emf.db.tests.model.Child#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Children</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Children</em>' containment reference list.
     * @see org.eclipse.emf.db.tests.model.ModelPackage#getRoot_Children()
     * @see org.eclipse.emf.db.tests.model.Child#getRoot
     * @model opposite="root" containment="true"
     * @generated
     */
    EList<Child> getChildren();

    /**
     * Returns the value of the '<em><b>Sub Root</b></em>' containment reference.
     * It is bidirectional and its opposite is '{@link org.eclipse.emf.db.tests.model.SubRoot#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Sub Root</em>' containment reference isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Sub Root</em>' containment reference.
     * @see #setSubRoot(SubRoot)
     * @see org.eclipse.emf.db.tests.model.ModelPackage#getRoot_SubRoot()
     * @see org.eclipse.emf.db.tests.model.SubRoot#getRoot
     * @model opposite="root" containment="true"
     * @generated
     */
    SubRoot getSubRoot();

    /**
     * Sets the value of the '{@link org.eclipse.emf.db.tests.model.Root#getSubRoot <em>Sub Root</em>}' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Sub Root</em>' containment reference.
     * @see #getSubRoot()
     * @generated
     */
    void setSubRoot(SubRoot value);

    /**
     * Returns the value of the '<em><b>Labels</b></em>' attribute list.
     * The list contents are of type {@link java.lang.String}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Labels</em>' attribute list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Labels</em>' attribute list.
     * @see org.eclipse.emf.db.tests.model.ModelPackage#getRoot_Labels()
     * @model
     * @generated
     */
    EList<String> getLabels();

} // Root
