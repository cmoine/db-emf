package org.eclipse.emf.db.query;


public class SelectWhereStep extends SelectGroupByStep {
    SelectWhereStep(SelectQuery query) {
        super(query);
    }

    // /* package */SelectWhereStep(EClass clazz) {
    // this.clazz=clazz;
    // }
    public SelectGroupByStep where(QueryExpression expression) {
        getQuery().where=expression;
        return new SelectGroupByStep(getQuery());
    }
}
