package org.eclipse.emf.db.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public final class DBQueryUtil {
	private DBQueryUtil() {
	}

	/**
	 * double les quotes dans les chaines pour les requetes sql et encadrement par un quote de la chaine
	 *
	 * @param chaine
	 * @return chaine
	 */
	public static String quote(String chaine) {
		chaine=StringUtils.replace(chaine, "'", "''"); //$NON-NLS-1$ //$NON-NLS-2$
		chaine=StringUtils.replace(chaine, "\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
		return new StringBuffer("'").append(chaine).append('\'').toString(); //$NON-NLS-1$
	}

	public static <T extends EObject> String getTableName(EClass eClass) {
		Assert.isTrue(!eClass.isAbstract());
		return getTableName((Class<EObject>) eClass.getInstanceClass());
	}

    public static <T extends EObject> String getTableName(EReference ref) {
        return getTableName(ref.getEContainingClass(), ref);
    }

    public static <T extends EObject> String getTableName(EClass clazz, EReference ref) {
        Assert.isTrue(ref.getUpperBound() == EReference.UNBOUNDED_MULTIPLICITY);
        return getTableName(clazz) + "_" + getColumnName(ref) + "_list"; //$NON-NLS-1$ //$NON-NLS-2$
    }

	public static <T extends EObject> String getTableName(Class<T> c) {
		return c.getSimpleName();
	}

	public static String getColumnName(EStructuralFeature feature) {
		return feature.getName();
	}

}
