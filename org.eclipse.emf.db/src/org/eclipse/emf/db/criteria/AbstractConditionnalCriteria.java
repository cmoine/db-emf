package org.eclipse.emf.db.criteria;

import org.apache.commons.lang3.ArrayUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.ecore.EObject;


/* package */ abstract class AbstractConditionnalCriteria implements ISearchCriteria {
	private ISearchCriteria[] criterias;

	AbstractConditionnalCriteria(ISearchCriteria... criterias) {
		this.criterias=criterias;
		Assert.isTrue(this.criterias.length >= 2);
	}

	public void add(ISearchCriteria criteria) {
		this.criterias=ArrayUtils.add(this.criterias, criteria);
	}

	@Override
	public String getExpression(Class<? extends EObject> clazz) {
		String result=criterias[0].getExpression(clazz);
		for (int i=1; i < criterias.length; i++) {
			result+=" " + getOperator() + " " + criterias[i].getExpression(clazz); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return "(" + result + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}

	protected abstract String getOperator();
}