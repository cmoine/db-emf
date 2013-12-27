package org.eclipse.emf.db.query;

public class SelectOffsetStep extends SelectFinalStep {
    SelectOffsetStep(SelectQuery query) {
        super(query);
    }

    public SelectFinalStep offset(int offset) {
        getQuery().offset=offset;
        return new SelectFinalStep(getQuery());
    }
}
