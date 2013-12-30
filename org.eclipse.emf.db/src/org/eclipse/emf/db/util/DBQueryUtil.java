package org.eclipse.emf.db.util;import java.sql.Connection;import java.sql.ResultSet;import java.sql.SQLException;import java.sql.Statement;import java.util.Arrays;import java.util.Collections;import java.util.Date;import java.util.List;import org.eclipse.core.runtime.Assert;import org.eclipse.core.runtime.IStatus;import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;import org.eclipse.emf.db.Activator;import org.eclipse.emf.ecore.EAttribute;import org.eclipse.emf.ecore.EClass;import org.eclipse.emf.ecore.EObject;import org.eclipse.emf.ecore.EReference;import org.eclipse.emf.ecore.EStructuralFeature;import org.eclipse.emf.ecore.ETypedElement;import com.google.common.collect.Lists;public final class DBQueryUtil {
    private static final int SQL_DATE_OFFSET=719528;    private static final List<String> MYSQL_RESERVED_WORDS=Arrays.asList("ACCESSIBLE", "ACTION", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "ASC", "ASENSITIVE", "BEFORE", "BETWEEN", "BIGINT", "BINARY", "BIT", "BLOB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR", "CHARACTER", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "CHECK", "COLLATE", "COLUMN", "COLUMNS", "CONDITION", "CONNECTION", "CONSTRAINT", "CONTINUE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "CONVERT", "CREATE", "CROSS", "CURRENT_DATE", "CURRENT_TIME", "CURRENT_TIMESTAMP", "CURRENT_USER", "CURSOR", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "DATABASE", "DATABASES", "DATE", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE", "DAY_SECOND", "DEC", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE", "DETERMINISTIC", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "DISTINCT", "DISTINCTROW", "DIV", "DOUBLE", "DROP", "DUAL", "EACH", "ELSE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "ELSEIF", "ENCLOSED", "ENUM", "ESCAPED", "EXISTS", "EXIT", "EXPLAIN", "FALSE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "FETCH", "FIELDS", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "FROM", "FULLTEXT", "GENERAL", "GOTO", "GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF", "IGNORE", "IGNORE_SERVER_IDS", "IN", "INDEX", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "INFILE", "INNER", "INOUT", "INSENSITIVE", "INSERT", "INT", "INT1", "INT2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "INT3", "INT4", "INT8", "INTEGER", "INTERVAL", "INTO", "IS", "ITERATE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "JOIN", "KEY", "KEYS", "KILL", "LABEL", "LEADING", "LEAVE", "LEFT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "LONG", "LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_HEARTBEAT_PERIOD", "MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT", "MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "MODIFIES", "NATURAL", "NO", "NOT", "NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER", "OUT", "OUTER", "OUTFILE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "PRECISION", "PRIMARY", "PRIVILEGES", "PROCEDURE", "PURGE", "RAID0", "RANGE", "READ", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "READS", "READ_WRITE", "REAL", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "REPLACE", "REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "REVOKE", "RIGHT", "RLIKE", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "SIGNAL", "SLOW", "SMALLINT", "SONAME", "SPATIAL", "SPECIFIC", "SQL", "SQL_BIG_RESULT", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "SQL_CALC_FOUND_ROWS", "SQLEXCEPTION", "SQL_SMALL_RESULT", "SQLSTATE", "SQLWARNING", "SSL", "STARTING", "STRAIGHT_JOIN", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "TABLE", "TABLES", "TERMINATED", "TEXT", "THEN", "TIME", "TIMESTAMP", "TINYBLOB", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE", "UNDO", "UNION", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "UPGRADE", "USAGE", "USE", "USING", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "UTC_DATE", "UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "WHEN", "WHERE", "WHILE", "WITH", "WRITE", "X509", "XOR", "YEAR_MONTH", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$            "ZEROFILL"); //$NON-NLS-1$    private static final long MILLIS_PER_DAY=24L * 60L * 60L * 1000L;    private DBQueryUtil() {
	}

	/**
	 * double les quotes dans les chaines pour les requetes sql et encadrement par un quote de la chaine
	 *
	 * @param chaine
	 * @return chaine
	 */
	public static String quote(String chaine) {
        chaine=chaine.replace("'", "''"); //$NON-NLS-1$ //$NON-NLS-2$        chaine=chaine.replace("\\", "\\\\"); //$NON-NLS-1$ //$NON-NLS-2$
		return new StringBuffer("'").append(chaine).append('\'').toString(); //$NON-NLS-1$
	}
    public static String getTableName(EClass eClass) {        Assert.isTrue(!eClass.isAbstract());        return getTableName((Class<EObject>) eClass.getInstanceClass());    }    public static <T extends EObject> String getTableName(Class<T> c) {        String result=c.getSimpleName();        return getTableName(result);    }    public static String getTableName(String result) {        if (MYSQL_RESERVED_WORDS.contains(result.toUpperCase()))            result+="0"; //$NON-NLS-1$        return result;    }
    // public static <T extends EObject> String getTableName(EClass eClass) {    // Assert.isTrue(!eClass.isAbstract());    // return getTableName((Class<EObject>) eClass.getInstanceClass());    // }    //    public static <T extends EObject> String getTableName(EAttribute ref) {        return getTableName(ref.getEContainingClass(), ref);    }    private static <T extends EObject> String getTableName(EClass clazz, EStructuralFeature ref) {        Assert.isTrue(ref.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY);        return getTableName(clazz) + "_" + getColumnName(ref) + "_list"; //$NON-NLS-1$ //$NON-NLS-2$    }    // public static <T extends EObject> String getTableName(Class<T> c) {    // return c.getSimpleName();    // }

	public static String getColumnName(EStructuralFeature feature) {        String result=feature.getName();        if (MYSQL_RESERVED_WORDS.contains(result.toUpperCase()))            result+="0"; //$NON-NLS-1$        return result;	}
    public static String getColumnNameExt(EStructuralFeature feature) {        if (feature instanceof EReference && ((EReference) feature).isContainer())            return CDODBSchema.ATTRIBUTES_CONTAINER;        String result=feature.getName();        if (MYSQL_RESERVED_WORDS.contains(result.toUpperCase()))            result+="0"; //$NON-NLS-1$        return result;    }    /**     * You must use TO_DAYS() in your SQL query     *      * @throws SQLException     */    public static Date queryDate(Connection view, Class<?> c, String selectString) throws SQLException {        // FIXME CDOQuery might support Date format        // String selectString=MLSQuery.getSQLExpression(query);        Assert.isTrue(selectString.toLowerCase().startsWith("select to_days("), "SQL query must start with SELECT TO_DAYS(..."); //$NON-NLS-1$ //$NON-NLS-2$        Long timestamp=queryLong(view, c, selectString);        if (timestamp != null && timestamp > 0L)            return new Date((timestamp - SQL_DATE_OFFSET) * MILLIS_PER_DAY);        return null;    }    public static long queryLong(Connection view, Class<?> c, final String selectString/* , ISearchCriteria searchCriteria */) throws SQLException {        List<Long> results=queryLongs(view, c, selectString);        if (results.isEmpty())            return -1L;        if (results.size() > 1)            Activator.log(IStatus.WARNING, "Query has multiple result, consider only the first result", null); //$NON-NLS-1$        return results.get(0);    }    public static List<Long> queryLongs(Connection view, Class<?> c, String selectString) throws SQLException {        Statement stmt=view.createStatement();        try {            ResultSet rSet=stmt.executeQuery(selectString);            if (rSet.last()) {                List<Long> result=Lists.newArrayListWithCapacity(rSet.getRow());                rSet.beforeFirst();                while (rSet.next()) {                    result.add(rSet.getLong(1));                }                return Collections.unmodifiableList(result);            } else {                return Collections.emptyList();            }            // } catch (SQLException e) {            //            Activator.log(IStatus.ERROR, "Failed to query", e); //$NON-NLS-1$        } finally {            // if(stmt!=null) {            // try {            stmt.close();            // } catch (SQLException e) {            //                    Activator.log(IStatus.ERROR, "Failed to close statement", e); //$NON-NLS-1$            // }            // }        }        // return null;    }    public static String queryString(Connection view, final String selectString) throws SQLException {        List<String> results=queryStrings(view, selectString);        if (results.isEmpty())            return null;        if (results.size() > 1)            Activator.log(IStatus.WARNING, "Query has multiple result, consider only the first result", null); //$NON-NLS-1$        return results.get(0);    }    public static List<String> queryStrings(final Connection view, String selectString) throws SQLException {        Statement stmt=view.createStatement();        try {            ResultSet rSet=stmt.executeQuery(selectString);            if (rSet.last()) {                List<String> result=Lists.newArrayListWithCapacity(rSet.getRow());                rSet.beforeFirst();                while (rSet.next()) {                    result.add(rSet.getString(1));                }                return Collections.unmodifiableList(result);            } else {                return Collections.emptyList();            }            // } catch (SQLException e) {            //            LOG.error("Failed to query", e); //$NON-NLS-1$        } finally {            stmt.close();        }        // return null;    }
}
