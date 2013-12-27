package org.eclipse.emf.db;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class DBModelInformationCache {
    private static final Map<EClass, Optional<EReference>> CONTAINER_REFERENCE=Maps.newHashMap();

    private static final Map<Integer, EClass> CDO_INTERNAL_CLASS_MAP=Maps.newHashMap();

    private DBModelInformationCache() {
    }

    public static EClass getEClassFromCdoClass(EPackage ePackage, int cdoInternalClass) {
        EClass result=CDO_INTERNAL_CLASS_MAP.get(cdoInternalClass);
        if (result == null) {
            for (EClassifier classifier : ePackage.getEClassifiers()) {
                if (classifier instanceof EClass) {
                    EClass eClass=(EClass) classifier;
                    if (eClass.getName().hashCode() == cdoInternalClass) {
                        result=eClass;
                        CDO_INTERNAL_CLASS_MAP.put(cdoInternalClass, result);
                    }
                }
            }
        }
        return result;
    }

    public static EReference getContainerReference(EClass clazz) {
        Optional<EReference> result=CONTAINER_REFERENCE.get(clazz);
        if (result == null) {
            List<EReference> refs=Lists.newArrayList();
            for (EReference ref : clazz.getEReferences()) {
                if (ref.getEOpposite() != null && ref.getEOpposite().isContainment()) {
                    refs.add(ref);
                }
            }
            if (refs.size() == 1) {
                result=Optional.of(refs.get(0));
            } else {
                result=Optional.absent();
            }
            CONTAINER_REFERENCE.put(clazz, result);
        }
        return result.orNull();
    }

    public static boolean hasInheritance(EReference ref) {
        if (ref == null)
            return false;
        if (ref.getEType() != null && ((EClass) ref.getEType()).isAbstract()) {
            EAnnotation annotation=ref.getEAnnotation("http://www.eclipse.org/DB-EMF");
            Assert.isTrue(annotation != null && Boolean.parseBoolean(annotation.getDetails().get("inheritance")), //
                    "You must annotate for an abstract type (" + ref.getEContainingClass().getName() + " -> " + ref.getName() + " -> "
                            + ref.getEType().getName() + ')');
            return true;
        } else {
            EAnnotation annotation=ref.getEAnnotation("http://www.eclipse.org/DB-EMF");
            if (annotation == null)
                return false;

            return Boolean.parseBoolean(annotation.getDetails().get("inheritance"));
        }
    }

    public static List<EClass> getConcreteClasses(EPackage pkg) {
        List<EClass> result=Lists.newArrayList(Iterables.filter(pkg.getEClassifiers(), EClass.class));
        for (Iterator<EClass> iterator=result.iterator(); iterator.hasNext();) {
            EClass clazz=iterator.next();
            if (clazz.isAbstract())
                iterator.remove();
        }
        return Collections.unmodifiableList(result);
    }

    public static EClass getEClass(EPackage pkg, Class<? extends DBObject> clazz) {
        for (EClass eClass : getConcreteClasses(pkg)) {
            if (eClass.getInstanceClass().equals(clazz))
                return eClass;
        }
        return null;
    }
}
