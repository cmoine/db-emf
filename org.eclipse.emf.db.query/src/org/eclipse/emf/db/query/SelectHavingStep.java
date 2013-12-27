package org.eclipse.emf.db.query;


public class SelectHavingStep extends SelectOrderByStep {
    SelectHavingStep(SelectQuery query) {
        super(query);
    }

    public SelectOrderByStep having(QueryExpression expression) {
        getQuery().having=expression;
        return new SelectOrderByStep(getQuery());
    }
}
