/**
 */
package org.eclipse.emf.db.tests.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each operation of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.emf.db.tests.model.ModelFactory
 * @model kind="package"
 * @generated
 */
public interface ModelPackage extends EPackage {
    /**
     * The package name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNAME = "model";

    /**
     * The package namespace URI.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_URI = "http://tests/1.0";

    /**
     * The package namespace name.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    String eNS_PREFIX = "tests";

    /**
     * The singleton instance of the package.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    ModelPackage eINSTANCE = org.eclipse.emf.db.tests.model.impl.ModelPackageImpl.init();

    /**
     * The meta object id for the '{@link org.eclipse.emf.db.tests.model.impl.RootImpl <em>Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.db.tests.model.impl.RootImpl
     * @see org.eclipse.emf.db.tests.model.impl.ModelPackageImpl#getRoot()
     * @generated
     */
    int ROOT = 0;

    /**
     * The feature id for the '<em><b>Children</b></em>' containment reference list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT__CHILDREN = 0;

    /**
     * The feature id for the '<em><b>Sub Root</b></em>' containment reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT__SUB_ROOT = 1;

    /**
     * The feature id for the '<em><b>Labels</b></em>' attribute list.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT__LABELS = 2;

    /**
     * The number of structural features of the '<em>Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_FEATURE_COUNT = 3;

    /**
     * The number of operations of the '<em>Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int ROOT_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.emf.db.tests.model.impl.ChildImpl <em>Child</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.db.tests.model.impl.ChildImpl
     * @see org.eclipse.emf.db.tests.model.impl.ModelPackageImpl#getChild()
     * @generated
     */
    int CHILD = 1;

    /**
     * The feature id for the '<em><b>Root</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHILD__ROOT = 0;

    /**
     * The number of structural features of the '<em>Child</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHILD_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Child</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int CHILD_OPERATION_COUNT = 0;

    /**
     * The meta object id for the '{@link org.eclipse.emf.db.tests.model.impl.SubRootImpl <em>Sub Root</em>}' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @see org.eclipse.emf.db.tests.model.impl.SubRootImpl
     * @see org.eclipse.emf.db.tests.model.impl.ModelPackageImpl#getSubRoot()
     * @generated
     */
    int SUB_ROOT = 2;

    /**
     * The feature id for the '<em><b>Root</b></em>' container reference.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUB_ROOT__ROOT = 0;

    /**
     * The number of structural features of the '<em>Sub Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUB_ROOT_FEATURE_COUNT = 1;

    /**
     * The number of operations of the '<em>Sub Root</em>' class.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     * @ordered
     */
    int SUB_ROOT_OPERATION_COUNT = 0;


    /**
     * Returns the meta object for class '{@link org.eclipse.emf.db.tests.model.Root <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Root</em>'.
     * @see org.eclipse.emf.db.tests.model.Root
     * @generated
     */
    EClass getRoot();

    /**
     * Returns the meta object for the containment reference list '{@link org.eclipse.emf.db.tests.model.Root#getChildren <em>Children</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference list '<em>Children</em>'.
     * @see org.eclipse.emf.db.tests.model.Root#getChildren()
     * @see #getRoot()
     * @generated
     */
    EReference getRoot_Children();

    /**
     * Returns the meta object for the containment reference '{@link org.eclipse.emf.db.tests.model.Root#getSubRoot <em>Sub Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the containment reference '<em>Sub Root</em>'.
     * @see org.eclipse.emf.db.tests.model.Root#getSubRoot()
     * @see #getRoot()
     * @generated
     */
    EReference getRoot_SubRoot();

    /**
     * Returns the meta object for the attribute list '{@link org.eclipse.emf.db.tests.model.Root#getLabels <em>Labels</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the attribute list '<em>Labels</em>'.
     * @see org.eclipse.emf.db.tests.model.Root#getLabels()
     * @see #getRoot()
     * @generated
     */
    EAttribute getRoot_Labels();

    /**
     * Returns the meta object for class '{@link org.eclipse.emf.db.tests.model.Child <em>Child</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Child</em>'.
     * @see org.eclipse.emf.db.tests.model.Child
     * @generated
     */
    EClass getChild();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.emf.db.tests.model.Child#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Root</em>'.
     * @see org.eclipse.emf.db.tests.model.Child#getRoot()
     * @see #getChild()
     * @generated
     */
    EReference getChild_Root();

    /**
     * Returns the meta object for class '{@link org.eclipse.emf.db.tests.model.SubRoot <em>Sub Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for class '<em>Sub Root</em>'.
     * @see org.eclipse.emf.db.tests.model.SubRoot
     * @generated
     */
    EClass getSubRoot();

    /**
     * Returns the meta object for the container reference '{@link org.eclipse.emf.db.tests.model.SubRoot#getRoot <em>Root</em>}'.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the meta object for the container reference '<em>Root</em>'.
     * @see org.eclipse.emf.db.tests.model.SubRoot#getRoot()
     * @see #getSubRoot()
     * @generated
     */
    EReference getSubRoot_Root();

    /**
     * Returns the factory that creates the instances of the model.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @return the factory that creates the instances of the model.
     * @generated
     */
    ModelFactory getModelFactory();

    /**
     * <!-- begin-user-doc -->
     * Defines literals for the meta objects that represent
     * <ul>
     *   <li>each class,</li>
     *   <li>each feature of each class,</li>
     *   <li>each operation of each class,</li>
     *   <li>each enum,</li>
     *   <li>and each data type</li>
     * </ul>
     * <!-- end-user-doc -->
     * @generated
     */
    interface Literals {
        /**
         * The meta object literal for the '{@link org.eclipse.emf.db.tests.model.impl.RootImpl <em>Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.emf.db.tests.model.impl.RootImpl
         * @see org.eclipse.emf.db.tests.model.impl.ModelPackageImpl#getRoot()
         * @generated
         */
        EClass ROOT = eINSTANCE.getRoot();

        /**
         * The meta object literal for the '<em><b>Children</b></em>' containment reference list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ROOT__CHILDREN = eINSTANCE.getRoot_Children();

        /**
         * The meta object literal for the '<em><b>Sub Root</b></em>' containment reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference ROOT__SUB_ROOT = eINSTANCE.getRoot_SubRoot();

        /**
         * The meta object literal for the '<em><b>Labels</b></em>' attribute list feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EAttribute ROOT__LABELS = eINSTANCE.getRoot_Labels();

        /**
         * The meta object literal for the '{@link org.eclipse.emf.db.tests.model.impl.ChildImpl <em>Child</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.emf.db.tests.model.impl.ChildImpl
         * @see org.eclipse.emf.db.tests.model.impl.ModelPackageImpl#getChild()
         * @generated
         */
        EClass CHILD = eINSTANCE.getChild();

        /**
         * The meta object literal for the '<em><b>Root</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference CHILD__ROOT = eINSTANCE.getChild_Root();

        /**
         * The meta object literal for the '{@link org.eclipse.emf.db.tests.model.impl.SubRootImpl <em>Sub Root</em>}' class.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @see org.eclipse.emf.db.tests.model.impl.SubRootImpl
         * @see org.eclipse.emf.db.tests.model.impl.ModelPackageImpl#getSubRoot()
         * @generated
         */
        EClass SUB_ROOT = eINSTANCE.getSubRoot();

        /**
         * The meta object literal for the '<em><b>Root</b></em>' container reference feature.
         * <!-- begin-user-doc -->
         * <!-- end-user-doc -->
         * @generated
         */
        EReference SUB_ROOT__ROOT = eINSTANCE.getSubRoot_Root();

    }

} //ModelPackage
