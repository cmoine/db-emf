package org.eclipse.emf.db.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.db.util.DBUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class QueryExpression {
    private static final SimpleDateFormat MYSQL_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    private QueryExpression() {
    }

    @Override
    public abstract String toString();

    public static QueryExpression from(DBObject obj) {
        return from(obj.cdoID());
    }

    public static QueryExpression from(final boolean b) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return b ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
            }
        };
    }

    public static QueryExpression from(final double d) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return Double.toString(d);
            }
        };
    }

    public static QueryExpression from(final float f) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return Float.toString(f);
            }
        };
    }

    public static QueryExpression from(final long l) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return Long.toString(l);
            }
        };
    }

    public static QueryExpression from(final int i) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return Integer.toString(i);
            }
        };
    }

    public static QueryExpression from(final String str) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return DBQueryUtil.quote(str);
            }
        };
    }

    public static QueryExpression from(final Date date) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return DBQueryUtil.quote(MYSQL_DATE_FORMAT.format(date));
            }
        };
    }

    public static QueryExpression from(final EStructuralFeature feature) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return DBQueryUtil.getColumnName(feature);
            }
        };
    }

    public QueryExpression in(final SelectFinalStep step) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return QueryExpression.this.toString() + " IN (" + step.getSQL() + ')';
            }
        };
    }

    public QueryExpression in(final DBObject... dbObjects) {
        return new QueryExpression() {
            @Override
            public String toString() {
                StringBuffer buf=new StringBuffer();
                for (DBObject dbObject : dbObjects) {
                    if (buf.length() > 0)
                        buf.append(',');
                    buf.append(from(dbObject));
                }
                return QueryExpression.this.toString() + " IN (" + buf + ')';
            }
        };
    }

    public QueryExpression notIn(final SelectFinalStep step) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return QueryExpression.this.toString() + " NOT IN (" + step.getSQL() + ')';
            }
        };
    }

    public static final QueryExpression ALL=new QueryExpression() {
        @Override
        public String toString() {
            return "*";
        }
    };

    public static final QueryExpression CDO_ID=new QueryExpression() {
        @Override
        public String toString() {
            return CDODBSchema.ATTRIBUTES_ID;
        }
    };

    public static final QueryExpression VERSION=new QueryExpression() {
        @Override
        public String toString() {
            return CDODBSchema.ATTRIBUTES_VERSION;
        }
    };

    public QueryExpression prefix(final EClass clazz) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder(DBQueryUtil.getTableName(clazz)).append('.').append(QueryExpression.this.toString()).toString();
            }
        };
    }

    public static QueryExpression from(final EReference ref) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return DBQueryUtil.getColumnNameExt(ref);
            }
        };
    }

    public static QueryExpression type(final EReference ref, EClass clazz) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return DBUtil.internalClass(ref);
            }
        }.eq(from(DBUtil.cdoInternalClass(clazz)));
    }

    public QueryExpression min() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("MIN(").append(QueryExpression.this.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression max() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("MAX(").append(QueryExpression.this.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression sum() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("SUM(").append(QueryExpression.this.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression not() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("NOT ").append(QueryExpression.this.toString()).toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression and(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append(" AND ").append(expr.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression or(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append(" OR ").append(expr.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression lt(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append('<').append(expr.toString()).append(')').toString();
            }
        };
    }

    public QueryExpression gt(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append('>').append(expr.toString()).append(')').toString();
            }
        };
    }

    public QueryExpression eq(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append('=').append(expr.toString()).append(')').toString();
            }
        };
    }

    public QueryExpression le(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append("<=").append(expr.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression ge(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder().append('(').append(QueryExpression.this.toString()).append(">=").append(expr.toString()).append(')').toString(); //$NON-NLS-1$
            }
        };
    }

    private static final SimpleDateFormat SDF_DAY=new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$

    private static final long MILLIS_PER_DAY=24L * 60L * 60L * 1000L;

    public QueryExpression between(final Date debut, Date fin) {
        final Date finalFin=new Date(fin.getTime() + MILLIS_PER_DAY);
        return new QueryExpression() {

            @Override
            public String toString() {
                return new StringBuilder(QueryExpression.this.toString())
                        .append(" BETWEEN ").append(SDF_DAY.format(debut)).append(" AND ").append(SDF_DAY.format(finalFin)).toString(); //$NON-NLS-1$ //$NON-NLS-2$
            }
        };
    }
}
