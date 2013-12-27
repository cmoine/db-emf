package org.eclipse.emf.db.query;

public class SelectLimitStep extends SelectFinalStep {
    SelectLimitStep(SelectQuery query) {
        super(query);
    }

    public SelectOffsetStep limit(int numberOfRows) {
        getQuery().limit=numberOfRows;
        return new SelectOffsetStep(getQuery());
    }
}
