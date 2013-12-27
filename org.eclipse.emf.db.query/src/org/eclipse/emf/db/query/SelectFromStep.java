package org.eclipse.emf.db.query;

import org.eclipse.emf.ecore.EClass;

public class SelectFromStep extends SelectFinalStep {
    /* package */SelectFromStep(SelectQuery query) {
        super(query);
    }

    public SelectWhereStep from(FromExpression iExpression) {
        getQuery().fromExpression=iExpression;
        return new SelectWhereStep(getQuery());
    }

    public SelectWhereStep from(EClass clazz) {
        // getQuery().from=QueryExpression.getTableName(clazz);
        // return new SelectWhereStep(getQuery());
        return from(new FromExpression(clazz));
    }
}
