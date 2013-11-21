package org.eclipse.emf.db.criteria;

import java.util.Collection;

public class OrCriteria extends AbstractConditionnalCriteria {

    public OrCriteria(Collection<ISearchCriteria> criterias) {
        this(criterias.toArray(new ISearchCriteria[criterias.size()]));
    }

	public OrCriteria(ISearchCriteria... criterias) {
		super(criterias);
	}

	@Override
	protected String getOperator() {
		return "OR"; //$NON-NLS-1$
	}
}