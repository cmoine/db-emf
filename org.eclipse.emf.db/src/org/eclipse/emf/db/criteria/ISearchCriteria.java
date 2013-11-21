package org.eclipse.emf.db.criteria;

import org.eclipse.emf.ecore.EObject;

public interface ISearchCriteria {
	public String getExpression(Class<? extends EObject> clazz);
}