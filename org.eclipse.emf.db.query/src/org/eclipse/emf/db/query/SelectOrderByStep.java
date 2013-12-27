package org.eclipse.emf.db.query;

import org.eclipse.emf.ecore.EStructuralFeature;

public class SelectOrderByStep extends SelectLimitStep {
    SelectOrderByStep(SelectQuery query) {
        super(query);
    }

    public SelectLimitStep orderBy(EStructuralFeature... features) {
        for (EStructuralFeature feature : features) {
            getQuery().orderBy.add(feature);
        }
        return new SelectLimitStep(getQuery());
    }

    // public SelectLimitStep orderBy(EStructuralFeature feature) {
    // getQuery().orderBy.add(feature);
    // return new SelectLimitStep(getQuery());
    // }
}
