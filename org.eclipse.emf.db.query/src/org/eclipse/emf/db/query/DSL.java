package org.eclipse.emf.db.query;

import java.util.Arrays;

import org.eclipse.emf.ecore.EPackage;

public class DSL {
    private final EPackage pkg;

    public DSL(EPackage pkg) {
        this.pkg=pkg;
    }

    // public SelectFromStep selectAll(EClass clazz) {
    // SelectQuery query=new SelectQuery(pkg);
    // query.select.add(QueryExpression.all(clazz));
    // return new SelectFromStep(query);
    // }

    public SelectFromStep select(QueryExpression... expressions) {
        SelectQuery query=new SelectQuery(pkg);
        query.select.addAll(Arrays.asList(expressions));
        return new SelectFromStep(query);
    }
}
