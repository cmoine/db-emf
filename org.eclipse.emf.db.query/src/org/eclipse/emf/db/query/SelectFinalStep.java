package org.eclipse.emf.db.query;


public class SelectFinalStep {
    private final SelectQuery query;

    /* package */SelectFinalStep(SelectQuery query) {
        this.query=query;
    }

    /* package */SelectQuery getQuery() {
        return query;
    }

    public String getSQL() {
        return query.toString();
    }
}
