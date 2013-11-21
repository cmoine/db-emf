package org.eclipse.emf.db.criteria;

import java.util.Collection;

public class AndCriteria extends AbstractConditionnalCriteria {

    public AndCriteria(Collection<ISearchCriteria> criterias) {
        this(criterias.toArray(new ISearchCriteria[criterias.size()]));
    }

	public AndCriteria(ISearchCriteria... criterias) {
		super(criterias);
	}

	@Override
	protected String getOperator() {
		return "AND"; //$NON-NLS-1$
	}
}