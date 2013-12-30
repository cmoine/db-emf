/**
 */
package org.eclipse.emf.db.tests.model.impl;

import org.eclipse.emf.db.tests.model.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ModelFactoryImpl extends EFactoryImpl implements ModelFactory {
    /**
     * Creates the default factory implementation.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public static ModelFactory init() {
        try {
            ModelFactory theModelFactory = (ModelFactory)EPackage.Registry.INSTANCE.getEFactory(ModelPackage.eNS_URI);
            if (theModelFactory != null) {
                return theModelFactory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ModelFactoryImpl();
    }

    /**
     * Creates an instance of the factory.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelFactoryImpl() {
        super();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
            case ModelPackage.ROOT: return (EObject)createRoot();
            case ModelPackage.CHILD: return (EObject)createChild();
            case ModelPackage.SUB_ROOT: return (EObject)createSubRoot();
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Root createRoot() {
        RootImpl root = new RootImpl();
        return root;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public Child createChild() {
        ChildImpl child = new ChildImpl();
        return child;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public SubRoot createSubRoot() {
        SubRootImpl subRoot = new SubRootImpl();
        return subRoot;
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @generated
     */
    public ModelPackage getModelPackage() {
        return (ModelPackage)getEPackage();
    }

    /**
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @deprecated
     * @generated
     */
    @Deprecated
    public static ModelPackage getPackage() {
        return ModelPackage.eINSTANCE;
    }

} //ModelFactoryImpl
