package org.eclipse.emf.db.query;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

public abstract class QueryExpression {
    private static final SimpleDateFormat MYSQL_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    // private static final Set<String> MYSQL_RESERVED_WORDS=new HashSet<String>(Arrays.asList(
    //            "ACCESSIBLE", "ACTION", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BIT", "BLOB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "CHECK", "COLLATE", "COLUMN", "COLUMNS", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "DATABASE", "DATABASES", "DATE", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "ELSEIF", "ENCLOSED", "ENUM", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "FETCH", "FIELDS", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "FROM", "FULLTEXT", "GENERAL", "GOTO", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IGNORE_SERVER_IDS", "IN", "INDEX", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "JOIN", "KEY", "KEYS", "KILL", "LABEL", "LEADING", "LEAVE", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_HEARTBEAT_PERIOD", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "MODIFIES", "NATURAL", "NO", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "PRECISION", "PRIMARY", "PRIVILEGES", "PROCEDURE", "PURGE", "RAID0", "RANGE", "READ", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "READS", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "SIGNAL", "SLOW", "SMALLINT", "SONAME", "SPATIAL", "SPECIFIC", "SQL", "SQL_BIG_RESULT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "SQL_CALC_FOUND_ROWS", "SQLEXCEPTION", "SQL_SMALL_RESULT", "SQLSTATE", "SQLWARNING", "SSL", "STARTING", "STRAIGHT_JOIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "TABLE", "TABLES", "TERMINATED", "TEXT", "THEN", "TIME", "TIMESTAMP", "TINYBLOB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "UPGRADE", "USAGE", "USE", "USING", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
    //            "ZEROFILL")); //$NON-NLS-1$

    private QueryExpression() {
    }

    @Override
    public abstract String toString();


    // /**
    // * double les quotes dans les chaines pour les requetes sql et encadrement par un quote de la chaine
    // *
    // * @param chaine
    // * @return chaine
    // */
    // public static String quote(String chaine) {
    //        chaine=chaine.replace("'", "''"); //$NON-NLS-1$ //$NON-NLS-2$
    //        chaine=chaine.replace("\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
    //        return new StringBuffer("'").append(chaine).append('\'').toString(); //$NON-NLS-1$
    // }
    //
    // public static <T extends EObject> String getTableName(EClass eClass) {
    // Assert.isTrue(!eClass.isAbstract());
    // return getTableName((Class<EObject>) eClass.getInstanceClass());
    // }
    //
    // public static <T extends EObject> String getTableName(Class<T> c) {
    // return c.getSimpleName();
    // }

    // public static QueryExpression all(final EClass clazz) {
    // return new QueryExpression() {
    // @Override
    // public String toString() {
    //                return new StringBuilder(getTableName(clazz)).append(".*").toString(); //$NON-NLS-1$
    // }
    // };
    // }

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
            return "cdo_id";
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

    public static QueryExpression inheritance(final EClass clazz) {
        return from(cdoInternalClass(clazz));
    }

    // FIXME Use DBUtil
    public static int cdoInternalClass(EClass eClass) {
        return eClass.getName().hashCode();
    }

    public QueryExpression min() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("MIN(").append(QueryExpression.this.toString()).append(')').toString();
            }
        };
    }

    public QueryExpression max() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("MAX(").append(QueryExpression.this.toString()).append(')').toString();
            }
        };
    }

    public QueryExpression not() {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder("NOT ").append(QueryExpression.this.toString()).toString();
            }
        };
    }

    public QueryExpression and(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append(" AND ").append(expr.toString()).toString();
            }
        };
    }

    public QueryExpression or(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append(" OR ").append(expr.toString()).toString();
            }
        };
    }

    public QueryExpression lt(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append('<').append(expr.toString()).toString();
            }
        };
    }

    public QueryExpression gt(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append('>').append(expr.toString()).toString();
            }
        };
    }

    public QueryExpression eq(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append('=').append(expr.toString()).toString();
            }
        };
    }

    public QueryExpression le(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append("<=").append(expr.toString()).toString(); //$NON-NLS-1$
            }
        };
    }

    public QueryExpression ge(final QueryExpression expr) {
        return new QueryExpression() {
            @Override
            public String toString() {
                return new StringBuilder('(').append(QueryExpression.this.toString()).append(">=").append(expr.toString()).toString(); //$NON-NLS-1$
            }
        };
    }

    // public static String getColumnName(EStructuralFeature feature) {
    // String result=feature.getName();
    // if (MYSQL_RESERVED_WORDS.contains(result.toUpperCase()))
    //            result+="0"; //$NON-NLS-1$
    // return result;
    // }
    //
    // public static String getColumnNameExt(EStructuralFeature feature) {
    // if (feature instanceof EReference && ((EReference) feature).isContainer())
    // return "cdo_container";
    //
    // String result=feature.getName();
    // if (MYSQL_RESERVED_WORDS.contains(result.toUpperCase()))
    //            result+="0"; //$NON-NLS-1$
    // return result;
    // }
}
