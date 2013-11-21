package org.eclipse.emf.db.criteria;

import org.eclipse.emf.ecore.EObject;


public class NotCriteria implements ISearchCriteria {
	private final ISearchCriteria criteria;

	public NotCriteria(ISearchCriteria criteria) {
		this.criteria=criteria;
	}

	@Override
	public String getExpression(Class<? extends EObject> clazz) {
		return "NOT (" + criteria.getExpression(clazz) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
}