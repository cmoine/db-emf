package org.eclipse.emf.db.query;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

class SelectQuery {
    List<QueryExpression> select=new ArrayList<QueryExpression>();

    // String from;
    FromExpression fromExpression;

    QueryExpression where;

    List<EStructuralFeature> orderBy=new ArrayList<EStructuralFeature>();

    List<EStructuralFeature> groupBy=new ArrayList<EStructuralFeature>();

    QueryExpression having;

    Integer limit, offset;

    final EPackage pkg;

    // List<InnerExpression> inner=new ArrayList<InnerExpression>();

    SelectQuery(EPackage pkg) {
        this.pkg=pkg;
    }

    @Override
    public String toString() {
        StringBuilder builder=new StringBuilder("SELECT "); //$NON-NLS-1$
        append(builder, select.toArray(), ',');
        builder.append(" FROM "); //$NON-NLS-1$

        fromExpression.appendPrefix(builder);
        fromExpression.appendSuffix(builder);

        if (where != null) {
            builder.append(" WHERE "); //$NON-NLS-1$
            builder.append(where);
        }

        if (!orderBy.isEmpty()) {
            builder.append(" ORDER BY "); //$NON-NLS-1$
            append(builder, orderBy.toArray(), ',');
        }

        if (!groupBy.isEmpty()) {
            builder.append(" GROUP BY "); //$NON-NLS-1$
            append(builder, groupBy.toArray(), ',');
        }

        if (having != null) {
            builder.append(" HAVING "); //$NON-NLS-1$
            builder.append(having);
        }

        if (limit != null) {
            builder.append(" LIMIT "); //$NON-NLS-1$
            if (offset != null)
                builder.append(offset).append(',');

            builder.append(limit);
        }

        return builder.toString();
    }

    private void append(StringBuilder builder, Object[] array, char c) {
        for (int i=0; i < array.length; i++) {
            if (i > 0)
                builder.append(c);

            Object object=array[i];
            if (object instanceof EStructuralFeature)
                builder.append(DBQueryUtil.getColumnName((EStructuralFeature) object));
            else
                builder.append(object.toString());
        }
    }
}
