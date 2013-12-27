package org.eclipse.emf.db.query;

import java.util.Arrays;

import org.eclipse.emf.ecore.EStructuralFeature;

public class SelectGroupByStep extends SelectHavingStep {
    SelectGroupByStep(SelectQuery query) {
        super(query);
    }

    public SelectHavingStep groupBy(EStructuralFeature... features) {
        getQuery().groupBy.addAll(Arrays.asList(features));
        return new SelectHavingStep(getQuery());
    }
}
