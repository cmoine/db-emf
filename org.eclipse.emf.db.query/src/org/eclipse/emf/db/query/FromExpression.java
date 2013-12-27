package org.eclipse.emf.db.query;

import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

public class FromExpression {
    private EClass src;

    public FromExpression(EClass src) {
        this.src=src;
    }

    public FromExpression innerJoin(EClass clazz, EReference ref) {
        if (ref.getEContainingClass().isSuperTypeOf(clazz) && ((EClass) ref.getEType()).isSuperTypeOf(src)) {
            // PERFECT
        } else if (ref.getEContainingClass().isSuperTypeOf(src) && ((EClass) ref.getEType()).isSuperTypeOf(clazz)) {
            // SWITCH references
            EClass tmp=clazz;
            clazz=src;
            src=tmp;
        } else {
            throw new AssertionFailedException("Invalid reference for the join" + ref);
        }
        return internalInnerJoin(clazz, ref);
    }

    private FromExpression internalInnerJoin(final EClass clazz, final EReference src) {
        return new FromExpression(clazz) {
            @Override
            void appendPrefix(StringBuilder builder) {
                FromExpression.this.appendPrefix(builder);
                builder.append(" INNER JOIN ").append(DBQueryUtil.getTableName(clazz));
            }

            @Override
            void appendSuffix(StringBuilder builder) {
                int length=builder.length();
                FromExpression.this.appendSuffix(builder);
                String suffix=(length != builder.length()) ? " AND " /* second inner join */: " ON ";
                builder.append(suffix).append(DBQueryUtil.getTableName(FromExpression.this.src)).append('.').append(DBQueryUtil.getColumnNameExt(src))
                        .append('=').append(DBQueryUtil.getTableName(clazz)).append(".cdo_id");
            }
        };
    }

    void appendPrefix(StringBuilder builder) {
        builder.append(DBQueryUtil.getTableName(src));
    }

    void appendSuffix(StringBuilder builder) {
    }
}
