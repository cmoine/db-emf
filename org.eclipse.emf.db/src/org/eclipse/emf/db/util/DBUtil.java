package org.eclipse.emf.db.util;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.cdo.server.internal.db.CDODBSchema;
import org.eclipse.emf.cdo.server.internal.db.DBAnnotation;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.db.Activator;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.db.RemoteException;
import org.eclipse.emf.db.impl.DBObjectImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;

public final class DBUtil {
    private static class MyProperties extends Properties {
        private static final long serialVersionUID=3509184653228847019L;

        public void close() {
        }
    }

    private static final SimpleDateFormat MYSQL_DATE_FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    private static BiMap<Long, String> resources=HashBiMap.create();

    private static final Function<EReference, String> INTERNAL_CLASS=new Function<EReference, String>() {
        @Override
        public String apply(EReference reference) {
            return DBQueryUtil.getColumnNameExt(reference) + "_internal_class"; //$NON-NLS-1$
        }
    };

    private static final Function<EReference, String> INTERNAL_CLASS_NAME=new Function<EReference, String>() {
        @Override
        public String apply(EReference reference) {
            return INTERNAL_CLASS.apply(reference) + "_name"; //$NON-NLS-1$
        }
    };

    private static final Map<Function<EReference, String>, String> INHERITANCE_COLUMN_NAMES=ImmutableMap.<Function<EReference, String>, String> builder()//
            .put(INTERNAL_CLASS, "INT(11)") //$NON-NLS-1$
            .put(INTERNAL_CLASS_NAME, "VARCHAR(256)").build(); //$NON-NLS-1$

    private static final int INITIAL_CAPACITY=10000;

    private static Cache<Long, DBObject> objects=CacheBuilder.newBuilder().initialCapacity(INITIAL_CAPACITY).weakValues().build();

    public static void clearObjectCache() {
        objects.invalidateAll();
        resources.clear();
    }

    private static ThreadLocal<Boolean> canSave=new ThreadLocal<Boolean>() {
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    private static ThreadLocal<List<Runnable>> post=new ThreadLocal<List<Runnable>>() {
        @Override
        protected List<Runnable> initialValue() {
            return Lists.newArrayList();
        }
    };

    private DBUtil() {
    }

    public static void diagnose(EPackage pkg) {
        for (EClass clazz : DBModelInformationCache.getConcreteClasses(pkg)) {
            // Check upper bounds
            for (EStructuralFeature feature : clazz.getEAllStructuralFeatures()) {
                if (feature.getLowerBound() != 0 && feature.getLowerBound() != 1)
                    throw new UnsupportedOperationException("Lower bound *must* be equals to zero or 1 " + feature); //$NON-NLS-1$
                if (feature.getUpperBound() != 1 && feature.getUpperBound() != ETypedElement.UNBOUNDED_MULTIPLICITY)
                    throw new UnsupportedOperationException("Upper bound *must* be equals to 1 or 'EStructuralFeature.UNBOUNDED_MULTIPLICITY' " + feature); //$NON-NLS-1$

                // Additionnal checks on EReferences
                if (feature instanceof EReference) {
                    EReference ref=(EReference) feature;
                    // 0..* relation to abstract is not allowed
                    if (ref.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY) {
                        if (ref.getEOpposite() == null || ref.getEOpposite().getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY)
                            throw new UnsupportedOperationException("Multiple relation must have a 0..1 EOpposite: " + toString(ref)); //$NON-NLS-1$

                        if (ref.getEType() instanceof EClass) {
                            EClass type=(EClass) ref.getEType();
                            if (type.isAbstract())
                                throw new UnsupportedOperationException("Multiple relation to abstract class is not supported for performance issues: " //$NON-NLS-1$
                                        + toString(ref));
                        }
                    }
                    // Check that 0..1 - 0..1 has at least one containment reference
                    if (ref.getUpperBound() == 1 && ref.getEOpposite() != null && ref.getEOpposite().getUpperBound() == 1) {
                        if ((!ref.isContainment()) && (!ref.getEOpposite().isContainment())) {
                            throw new UnsupportedOperationException("0..1 - 0..1 relation must have at least one containment reference: " + toString(ref)); //$NON-NLS-1$
                        }
                    }
                    // Check the reference without caring about the result
                    DBModelInformationCache.hasInheritance(ref);
                } else if (feature instanceof EAttribute) {
                    if (feature.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY)
                        throw new UnsupportedOperationException("Multiple relations are forbidden for attributes: " + toString(feature)); //$NON-NLS-1$
                }
            }
        }
    }

    /* package */static String toString(EStructuralFeature ref) {
        return ref.getEContainingClass().getName() + " -> " + ref.getName() + " -> " + ref.getEType().getName(); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static void createOrUpdateDBStructure(Connection connection, EPackage pkg) throws SQLException {
        diagnose(pkg);
        Statement statement=connection.createStatement();
        try {
            List<String> tableNames=Lists.newArrayList();
            ResultSet resultSet=statement.executeQuery("show tables"); //$NON-NLS-1$
            try {
                while (resultSet.next()) {
                    tableNames.add(resultSet.getString(1).toLowerCase());
                }
            } finally {
                resultSet.close();
            }
            for (EClass clazz : DBModelInformationCache.getConcreteClasses(pkg)) {
                List<String> columnNames=Lists.newArrayList();
                String tableName=DBQueryUtil.getTableName(clazz);
                if (!tableNames.remove(tableName.toLowerCase())) {
                    String createTableQuery=MessageFormat
                            .format("CREATE TABLE `{0}` (`cdo_id` bigint(20) NOT NULL AUTO_INCREMENT,`cdo_version` int(11) NOT NULL,`cdo_created` bigint(20) NOT NULL, `cdo_revised` bigint(20) NOT NULL, `cdo_resource` bigint(20), `cdo_container` bigint(20), `cdo_feature` int(11) NOT NULL,  UNIQUE KEY `{0}_idx0` (`cdo_id`,`cdo_version`),  KEY `{0}_idx1` (`cdo_id`,`cdo_revised`), KEY `{0}_idx2` (`cdo_container`,`cdo_version`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;", tableName); //$NON-NLS-1$
                    statement.execute(createTableQuery);
                } else {
                    resultSet=statement.executeQuery(MessageFormat.format("describe {0};", tableName)); //$NON-NLS-1$
                    try { 
                        while (resultSet.next()) {
                            String columnName=resultSet.getString("Field"); //$NON-NLS-1$
                            columnNames.add(columnName);
                        }
                        // Table does not exists
                    } finally {
                        resultSet.close();
                    }
                }

                // Create column for inheritance
                for (EReference ref : clazz.getEAllReferences()) {
                    if (DBModelInformationCache.hasInheritance(ref)) {
                        String type=ref.isContainment() ? "BIGINT(20)" : "INT(11)"; //$NON-NLS-1$//$NON-NLS-2$
                        for (Entry<Function<EReference, String>, String> entry : INHERITANCE_COLUMN_NAMES.entrySet()) {
                            String columnName=entry.getKey().apply(ref);
                            if (!columnNames.remove(columnName)) {
                                String query="ALTER TABLE " + tableName + " ADD COLUMN "
                                        + MessageFormat.format("`" + columnName + "` " + entry.getValue() + " NULL DEFAULT NULL", columnName, type); //$NON-NLS-1$
                                statement.execute(query);
                                
                                String dropIndex="ALTER TABLE " + tableName + " DROP INDEX " + tableName + "_idx2";
                                statement.execute(dropIndex);
                                String createIndex="ALTER TABLE " + tableName + " ADD INDEX " + tableName + "_idx2 (`" + INTERNAL_CLASS.apply(ref)
                                        + "`,`cdo_container`,`cdo_version`)";
                                statement.execute(createIndex);
                            }
                        }
                    }
                }
                // Create column for EStructuralFeatures
                for (EStructuralFeature feature : clazz.getEAllStructuralFeatures()) {
                    if (feature.getUpperBound() == 1) {
                        // if (feature instanceof EAttribute) {
                        // EAttribute attribute=(EAttribute) feature;
                        // String subTableName=DBQueryUtil.getTableName(attribute);
                        // if (!tableNames.remove(subTableName.toLowerCase())) {
                        // String query=MessageFormat
                        //                                        .format("CREATE TABLE `{0}` (`cdo_source` bigint(20) DEFAULT NULL, `cdo_idx` int(11) DEFAULT NULL, {1}, UNIQUE KEY `{0}_idx0` (`cdo_source`,`cdo_idx`) ) ENGINE=InnoDB DEFAULT CHARSET=utf8;", //$NON-NLS-1$
                        // subTableName, getSubQuery(attribute, CDODBSchema.LIST_VALUE));
                        // statement.execute(query);
                        // }
                        // }
                        // } else {
                        String columnName=DBQueryUtil.getColumnName(feature);
                        String query=null;
                        if (feature instanceof EAttribute) {
                            EAttribute attribute=(EAttribute) feature;
                            if (attribute.getUpperBound() == 1 && !columnNames.remove(columnName)) {
                                query=getSubQuery(attribute, columnName);
                            }
                        }
                        if (feature instanceof EReference) {
                            EReference ref=(EReference) feature;
                            // "ALTER TABLE trame ADD COLUMN `posologie` BIGINT(20) NULL DEFAULT NULL  AFTER `moments` ;"
                            if (!columnNames.remove(columnName)) {
                                String type=ref.isContainment() ? "BIGINT(20)" : "INT(11)"; //$NON-NLS-1$//$NON-NLS-2$
                                query=MessageFormat.format("`{0}` {1} NULL DEFAULT NULL", columnName, type); //$NON-NLS-1$
                            }
                        }
                        if (query != null) {
                            query="ALTER TABLE " + tableName + " ADD COLUMN " + query + ";"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            statement.execute(query);
                        }
                    }
                }
                columnNames.remove(CDODBSchema.ATTRIBUTES_ID);
                columnNames.remove(CDODBSchema.ATTRIBUTES_VERSION);

                columnNames.remove(CDODBSchema.ATTRIBUTES_CREATED);
                columnNames.remove(CDODBSchema.ATTRIBUTES_REVISED);
                columnNames.remove(CDODBSchema.ATTRIBUTES_RESOURCE);
                columnNames.remove(CDODBSchema.ATTRIBUTES_CONTAINER);
                columnNames.remove(CDODBSchema.ATTRIBUTES_FEATURE);
                if (!columnNames.isEmpty()) {
                    Activator.log(IStatus.WARNING, MessageFormat.format("The column {1} in table {0} got unused", tableName, columnNames), null); //$NON-NLS-1$
                }
                connection.commit();
                // }
            }
            if (!tableNames.contains("cdoresource")) { //$NON-NLS-1$
                String createTableQuery="CREATE TABLE `cdoresource` (`cdo_id` bigint(20) NOT NULL AUTO_INCREMENT,`name` VARCHAR(1024) NOT NULL,  UNIQUE KEY `cdoresource_idx0` (`cdo_id`,`name`(4))) ENGINE=InnoDB DEFAULT CHARSET=utf8;"; //$NON-NLS-1$
                statement.execute(createTableQuery);
                connection.commit();
            }
        } finally {
            statement.close();
        }
    }

    /**
     * *Must* be called after {@link #createOrUpdateDBStructure(Connection, EPackage)}
     */
    public static void migrateFromCDO(Connection connection, EPackage pkg) throws SQLException {
        migrateFromCDO(connection, pkg, null, null);
    }

    private static final int PARTITION_SIZE=1000;

    /**
     * *Must* be called after {@link #createOrUpdateDBStructure(Connection, EPackage)}
     */
    public static void migrateFromCDO(Connection connection, EPackage pkg, String taskName, IProgressMonitor monitor) throws SQLException {
        Statement stmt=connection.createStatement();
        try {
            List<EClass> concreteClasses=DBModelInformationCache.getConcreteClasses(pkg);
            monitor.beginTask(taskName, concreteClasses.size());
            for (EClass clazz : concreteClasses) {
                stmt.execute("ALTER TABLE " + DBQueryUtil.getTableName(clazz) + " CHANGE COLUMN `cdo_id` `cdo_id` BIGINT(20) NOT NULL AUTO_INCREMENT ;");
                for (EReference ref : clazz.getEAllReferences()) {
                    if (DBModelInformationCache.hasInheritance(ref)) {
                        for (EClass clazz2 : concreteClasses) {
                            if( ((EClass)ref.getEType()).isSuperTypeOf(clazz2) && !clazz2.isAbstract() ) {
                                ResultSet resultSet=stmt.executeQuery("SELECT " + CDODBSchema.ATTRIBUTES_ID + " FROM " + DBQueryUtil.getTableName(clazz2));
                                try {
                                    if (resultSet.last()) {
                                        List<Long> values=Lists.newArrayListWithCapacity(resultSet.getRow());
                                        resultSet.beforeFirst();
                                        while (resultSet.next()) {
                                            values.add(resultSet.getLong(1));
                                        }
                                        for (List<Long> ids : Iterables.partition(values, PARTITION_SIZE)) {
                                            stmt.execute("UPDATE " + DBQueryUtil.getTableName(clazz) + " SET " + INTERNAL_CLASS.apply(ref) + '='
                                                    + cdoInternalClass(clazz2) + ',' + INTERNAL_CLASS_NAME.apply(ref) + '='
                                                    + DBQueryUtil.quote(cdoInternalClassName(clazz2)) + " WHERE " + DBQueryUtil.getColumnName(ref) + " IN ("
                                                    + Joiner.on(',').join(ids) + ")");
                                        }
                                    }
                                } finally {
                                    resultSet.close();
                                }
                            }
                        }
                    }
                }
                monitor.worked(1);
            }
            monitor.done();
        } finally {
            stmt.close();
        }
    }

    private static String getSubQuery(EAttribute attribute, String columnName) {
        String type;
        type=EcoreUtil.getAnnotation(attribute, DBAnnotation.SOURCE_URI, DBAnnotation.COLUMN_TYPE);
        if (type != null) {
            String length=EcoreUtil.getAnnotation(attribute, DBAnnotation.SOURCE_URI, DBAnnotation.COLUMN_LENGTH);
            if (length != null)
                type+='(' + length + ')';
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEString())) {
            type="longtext"; //$NON-NLS-1$
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEBoolean())) {
            type="tinyint(1)"; //$NON-NLS-1$
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEInt())) {
            type="int(11)"; //$NON-NLS-1$
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEFloat())) {
            type="float"; //$NON-NLS-1$
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEDouble())) {
            type="double"; //$NON-NLS-1$
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEByteArray())) {
            type="longblob"; //$NON-NLS-1$
        } else if (attribute.getEType().equals(EcorePackage.eINSTANCE.getEDate())) {
            type="timestamp"; //$NON-NLS-1$
        } else if (attribute.getEType() instanceof EEnum) {
            type="INT(11)"; //$NON-NLS-1$
        } else {
            throw new UnsupportedOperationException("Unsupported type : " + attribute.getEType()); //$NON-NLS-1$
        }
        return MessageFormat.format("`{0}` {1} NULL DEFAULT NULL", columnName, type); //$NON-NLS-1$
    }

    /**
     * QLE : to use this method place your method in {@link ModelQueryUtil}
     */
    public static <T extends DBObject> List<T> getAll(Connection view, Class<T> c, EPackage pkg, String queryString) throws SQLException {
        Statement stmt=view.createStatement();
        try {
            ResultSet rSet=stmt.executeQuery(queryString);
            if (rSet.last()) {
                List<T> result=Lists.newArrayListWithCapacity(rSet.getRow());
                rSet.beforeFirst();
                Map<String, Integer> mapping=null;
                EClass eClass=DBModelInformationCache.getEClass(pkg, c);
                while (rSet.next()) {
                    // EClass eClass=ModelUtil.ECLASSES.get(c);
                    long cdoId=rSet.getLong(CDODBSchema.ATTRIBUTES_ID);
                    T item=(T) getObjectFromCache(view, eClass, cdoId);
                    if (item == null) {
                        item=(T) pkg.getEFactoryInstance().create(eClass);
                        ((DBObjectImpl) item).setCdoID(cdoId);
                        putObjectInCache(item);
                    }

                    mapping=fetch2(rSet, item, mapping);
                    result.add(item);
                }
                return Collections.unmodifiableList(result);
            } else {
                return Collections.emptyList();
            }
        } finally {
            stmt.close();
        }
    }

    public static <T extends DBObject> T query(Connection connection, long cdoID, Class<T> c, EPackage pkg) throws SQLException {
        if (cdoID < 1) {
            Activator.log(IStatus.WARNING, "DBUtil.query(..) cdoID < 1 -> return NULL ", null); //$NON-NLS-1$
            return null;
        }
        EClass eClass=DBModelInformationCache.getEClass(pkg, c);
        T result=(T) getObjectFromCache(connection, eClass, cdoID);
        if (result == null) {
            result=(T) pkg.getEFactoryInstance().create(eClass);
            reload(connection, result, cdoID);
            putObjectInCache(result);
        }
        return result;
    }

    private static long key(DBObject obj) {
        return key(obj.eClass(), obj.cdoID());
    }

    private static long key(EClass clazz, long cdoid) {
        return ((long) clazz.getClassifierID() & 0xFFF) | cdoid << 12;
    }

    private static DBObject getObjectFromCache(Connection connection, EClass eClass, long cdoId) throws SQLException {
        long key=key(eClass, cdoId);
        DBObject result=objects.getIfPresent(key);
        return result;
    }

    private static void putObjectInCache(DBObject dbObject) {
        long key=key(dbObject);
        objects.put(key, dbObject);
    }

    private static String getResource(Connection con, Long resourceId) throws SQLException {
        if (!resources.containsKey(resourceId)) {
            Statement stmt=con.createStatement();
            try {
                ResultSet rSet=stmt.executeQuery("SELECT name FROM cdoresource WHERE " + CDODBSchema.ATTRIBUTES_ID + '=' + resourceId);
                while (rSet.next()) {
                    resources.put(resourceId, Strings.nullToEmpty(rSet.getString(1)));
                }
            } finally {
                stmt.close();
            }
        }
        return resources.get(resourceId);
    }

    private static Long getResourceId(Connection con, String resource) throws SQLException {
        if (!resources.containsValue(resource)) {
            Statement stmt=con.createStatement();
            try {
                ResultSet rSet=stmt.executeQuery("SELECT " + CDODBSchema.ATTRIBUTES_ID + " FROM cdoresource WHERE name='" + resource + '\'');
                while (rSet.next()) {
                    resources.put(rSet.getLong(1), resource);
                }
            } finally {
                stmt.close();
            }
        }
        return resources.inverse().get(resource);
    }

    public static void reload(Connection con, DBObject obj) throws SQLException {
        reload(con, obj, obj.cdoID());
    }

    private static void reload(Connection con, DBObject obj, long cdoId) throws SQLException {
        Statement stmt=con.createStatement();
        try {
            ResultSet rSet=stmt.executeQuery("SELECT * FROM " + DBQueryUtil.getTableName(obj.eClass()) + " WHERE " + CDODBSchema.ATTRIBUTES_ID + '=' + cdoId);
            if (rSet.next()) {
                fetch(rSet, obj, null);
            }
        } finally {
            stmt.close();
        }
    }

    private static Map<String, Integer> fetch(ResultSet rSet, DBObject obj, Map<String, Integer> mapping) throws SQLException {
        ((DBObjectImpl) obj).setCdoID(rSet.getLong(CDODBSchema.ATTRIBUTES_ID));
        return fetch2(rSet, obj, mapping);
    }

    private static Map<String, Integer> fetch2(ResultSet rSet, DBObject obj, Map<String, Integer> mapping) throws SQLException {
        // ((DBObjectImpl) obj).setCdoID(rSet.getLong(CDODBSchema.ATTRIBUTES_ID));
        obj.cdoSetResource(getResource(rSet.getStatement().getConnection(), rSet.getLong(CDODBSchema.ATTRIBUTES_RESOURCE)));
        ((DBObjectImpl) obj).setRevision(rSet.getLong(CDODBSchema.ATTRIBUTES_CREATED));
        ((DBObjectImpl) obj).setConnection(rSet.getStatement().getConnection());

        // Copy Attributes
        for (EAttribute att : obj.eClass().getEAllAttributes()) {
            if (att.getUpperBound() != ETypedElement.UNBOUNDED_MULTIPLICITY) {
                String column=DBQueryUtil.getColumnName(att);
                Integer columnIndex;
                if (mapping == null) {
                    columnIndex=rSet.findColumn(column);
                } else {
                    columnIndex=mapping.get(column);
                    if (columnIndex == null) {
                        columnIndex=rSet.findColumn(column);
                        mapping.put(column, columnIndex);
                    }
                }

                if (att.getEType().equals(EcorePackage.eINSTANCE.getEString())) {
                    obj.eSet(att, rSet.getString(columnIndex));
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getEDate())) {
                    Object date=rSet.getObject(columnIndex);
                    if (date instanceof Timestamp) {
                        obj.eSet(att, new java.util.Date(((Timestamp) date).getTime()));
                    } else if (date instanceof java.sql.Date) {
                        obj.eSet(att, new java.util.Date(((java.sql.Date) date).getTime()));
                    } else if (date != null) {
                        throw new UnsupportedOperationException();
                    }
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getEInt())) {
                    obj.eSet(att, rSet.getInt(columnIndex));
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getELong())) {
                    obj.eSet(att, rSet.getLong(columnIndex));
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getEFloat())) {
                    obj.eSet(att, rSet.getFloat(columnIndex));
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getEDouble())) {
                    obj.eSet(att, rSet.getDouble(columnIndex));
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getEBoolean())) {
                    obj.eSet(att, rSet.getBoolean(columnIndex));
                } else if (att.getEType().equals(EcorePackage.eINSTANCE.getEByteArray())) {
                    obj.eSet(att, rSet.getBytes(columnIndex));
                } else if (att.getEType() instanceof EEnum) {
                    try {
                        Method method=att.getEType().getInstanceClass().getMethod("get", int.class); //$NON-NLS-1$
                        obj.eSet(att, method.invoke(null, rSet.getInt(columnIndex)));
                    } catch (ReflectiveOperationException e) {
                        Activator.log(IStatus.ERROR, "Failed to get enum value", e); //$NON-NLS-1$
                    }

                } else {
                    throw new UnsupportedOperationException(att.getEType().getName());
                }
            }
        }
        // Copy EReferences
        for (EReference ref : obj.eClass().getEAllReferences()) {
            if (ref.getUpperBound() == 1 && !ref.isContainment()) {
                // 0..1
                String column=DBQueryUtil.getColumnName(ref);
                Integer columnIndex;
                if (mapping == null) {
                    columnIndex=rSet.findColumn(column);
                } else {
                    columnIndex=mapping.get(column);
                    if (columnIndex == null) {
                        columnIndex=rSet.findColumn(column);
                        mapping.put(column, columnIndex);
                    }
                }
                Long cdoId=rSet.getLong(columnIndex);
                if (cdoId == 0L)
                    cdoId=null;

                ((DBObjectImpl) obj).map().put(ref, cdoId);
            }
        }

        ((DBObjectImpl) obj).map().put(obj.eContainmentFeature(), rSet.getLong(CDODBSchema.ATTRIBUTES_CONTAINER));
        ((DBObjectImpl) obj).dbClearModified();

        return mapping;
    }

    private static abstract class MyRunnable implements Runnable {
        private final DBObject obj;

        public MyRunnable(DBObject obj) {
            this.obj=obj;
        }

        @Override
        public void run() {
            if (!isStoredInDB(obj))
                throw new ConcurrentModificationException("Object was deleted in the meantime.");
            safeRun(obj);
        }

        protected abstract void safeRun(DBObject obj);
    }

    /* package */static void doSave(Connection connection, final DBObject obj, Collection<EStructuralFeature> features) throws SQLException {
        if (!canSave.get())
            throw new DBException("You must execute this in a transaction");

        Statement stmt=connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
        ((DBObjectImpl) obj).setConnection(connection);

        MyProperties props=null;
        try {
            if (obj.cdoID() == -1) {
                props=getValuesAsProperties(stmt, obj, obj.eClass().getEAllStructuralFeatures());
                props.setProperty(CDODBSchema.ATTRIBUTES_VERSION, "1"); //$NON-NLS-1$
                props.setProperty(CDODBSchema.ATTRIBUTES_REVISED, "0"); //$NON-NLS-1$
                props.setProperty(CDODBSchema.ATTRIBUTES_FEATURE, "0"); //$NON-NLS-1$
                if (!props.containsKey(CDODBSchema.ATTRIBUTES_RESOURCE))
                    props.setProperty(CDODBSchema.ATTRIBUTES_RESOURCE, "0"); //$NON-NLS-1$

                String[] keys=new String[props.size()];
                String[] values=new String[props.size()];
                int i=0;
                for (String key : Iterables.filter(props.keySet(), String.class)) {
                    keys[i]=key;
                    values[i]=props.getProperty(key);
                    i++;
                }

                stmt.executeUpdate("INSERT INTO " + DBQueryUtil.getTableName(obj.eClass()) //
                        + " (" + Joiner.on(',').join(keys) + ") VALUES (" + Joiner.on(',').join(values) + ")",
                        Statement.RETURN_GENERATED_KEYS);
                ResultSet generatedKeys=stmt.getGeneratedKeys();
                generatedKeys.next();
                long cdoId=generatedKeys.getLong(1);
                ((DBObjectImpl) obj).setCdoID(cdoId);
                generatedKeys.close();

                ((DBObjectImpl) obj).setRevision(Long.parseLong(props.getProperty(CDODBSchema.ATTRIBUTES_CREATED)));
                putObjectInCache(obj);

                post.get().add(new MyRunnable(obj) {
                    @Override
                    protected void safeRun(DBObject obj) {
                        fireCreated(obj);
                    }
                });
                // Assume that all 0..* relations are empty
                // for (EReference ref : obj.eClass().getEReferences()) {
                // if (ref.getUpperBound() == ETypedElement.UNBOUNDED_MULTIPLICITY)
                // ((DBObjectImpl) obj).map().put(ref, new DBList(ref, obj));
                // }
            } else {
                props=getValuesAsProperties(stmt, obj, features);
                StringBuilder builder=new StringBuilder();
                for (String key : Iterables.filter(props.keySet(), String.class)) {
                    if (builder.length() > 0)
                        builder.append(',');
                    builder.append(key).append('=').append(props.getProperty(key));
                }
                stmt.executeUpdate("UPDATE " + DBQueryUtil.getTableName(obj.eClass()) + " SET " + builder + " WHERE " + CDODBSchema.ATTRIBUTES_ID + '='
                        + obj.cdoID() + " AND " + CDODBSchema.ATTRIBUTES_CREATED + '=' + obj.cdoRevision());
                int nb=stmt.getUpdateCount();
                // Check if the revision hasn't changed
                if (nb == 0) {
                    // Try to analyze deeper the problem: was it deleted or saved somewhere else ?
                    ResultSet rSet=stmt.executeQuery("SELECT " + CDODBSchema.ATTRIBUTES_CREATED + " FROM " + DBQueryUtil.getTableName(obj.eClass()) + " WHERE "
                            + CDODBSchema.ATTRIBUTES_ID + '=' + obj.cdoID());
                    try {
                        if (rSet.next())
                            throw new ConcurrentModificationException("Revision is " + rSet.getLong(1) + " instead of " + obj.cdoRevision());
                        else
                            throw new ConcurrentModificationException("Object " + obj + " was already deleted");
                    } finally {
                        rSet.close();
                    }
                }

                ((DBObjectImpl) obj).setRevision(Long.parseLong(props.getProperty(CDODBSchema.ATTRIBUTES_CREATED)));

                post.get().add(new MyRunnable(obj) {
                    @Override
                    protected void safeRun(DBObject obj) {
                        fireModified(obj);
//                        WebSocketService.INSTANCE.broadcastModified(obj);
                    }
                });
            }
            ((DBObjectImpl) obj).dbClearModified();
            ((DBObjectImpl) obj).dbClearDetached();
        } finally {
            stmt.close();
            if (props != null)
                props.close();
        }
    }
    
    private static final List<IDBListener> listeners=Collections.synchronizedList(new ArrayList<IDBListener>());

    public static void addListener(IDBListener listener) {
        listeners.add(listener);
    }

    public static void removeListener(IDBListener listener) {
        listeners.remove(listener);
    }

    private static void fireModified(DBObject obj) {
        for (IDBListener listener : listeners) {
            try {
                listener.modified(obj);
            } catch (Throwable t) {
                Activator.log(IStatus.ERROR, "Internal error while notifying modification", t); //$NON-NLS-1$
            }
        }
    }

    private static void fireCreated(DBObject obj) {
        for (IDBListener listener : listeners) {
            try {
                listener.created(obj);
            } catch (Throwable t) {
                Activator.log(IStatus.ERROR, "Internal error while notifying creation", t); //$NON-NLS-1$
            }
        }
    }

    private static MyProperties getValuesAsProperties(Statement stmt, final DBObject obj, Collection<EStructuralFeature> features) throws SQLException {
        Connection connection=stmt.getConnection();
        MyProperties values=new MyProperties();
        for (EAttribute att : Iterables.filter(features, EAttribute.class)) {
            if (att.getUpperBound() != ETypedElement.UNBOUNDED_MULTIPLICITY) {
                Object value=obj.eGet(att);
                if (att.getEType().equals(EcorePackage.eINSTANCE.getEBoolean()))
                    value=(value != null && (Boolean) value) ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
                else if (att.getEType().equals(EcorePackage.eINSTANCE.getEString()))
                    value=value == null ? null : DBQueryUtil.quote((String) value);
                else if (value instanceof Enumerator)
                    value=((Enumerator) value).getValue();
                else if (att.getEType().equals(EcorePackage.eINSTANCE.getEDate()))
                    value=value == null ? null : DBQueryUtil.quote(MYSQL_DATE_FORMAT.format((java.util.Date) value));
                else if (att.getEType().equals(EcorePackage.eINSTANCE.getEByteArray()))
                    value=value == null ? null : "x'" + BaseEncoding.base16().encode((byte[]) value) + '\''; //$NON-NLS-1$ 

                values.setProperty(DBQueryUtil.getColumnName(att), Objects.toString(value));
            }
        }
        for (EReference ref : Iterables.filter(features, EReference.class)) {
            // EReference opposite=ref.getEOpposite();
            if (ref.getUpperBound() != ETypedElement.UNBOUNDED_MULTIPLICITY) {
                DBObject value=(DBObject) obj.eGet(ref);
                values.setProperty(DBQueryUtil.getColumnName(ref), Objects.toString((value == null ? null : value.cdoID())));
            }
        }
        long newRevision=System.currentTimeMillis();
        values.setProperty(CDODBSchema.ATTRIBUTES_CREATED, Long.toString(newRevision));
        if (obj.cdoResource() != null) {
            Long res=getResourceId(connection, obj.cdoResource());
            if (res == null) {
                stmt.executeUpdate("INSERT INTO cdoresource (name) VALUES ('" + obj.cdoResource() + "')", Statement.RETURN_GENERATED_KEYS);
                ResultSet generatedKeys=stmt.getGeneratedKeys();
                generatedKeys.next();
                res=generatedKeys.getLong(1);
                resources.put(res, obj.cdoResource());
            }
            values.setProperty(CDODBSchema.ATTRIBUTES_RESOURCE, Long.toString(res));
        }
        if (obj.eContainer() != null) {
            DBObject eContainer=((DBObject) obj.eContainer());
            Assert.isTrue(DBUtil.isStoredInDB(eContainer), "You must commit parent before");
            values.setProperty(CDODBSchema.ATTRIBUTES_CONTAINER, Long.toString(eContainer.cdoID()));

            // TODO CME et QLE
            List<EReference> references=Lists.newArrayList(Collections2.filter(eContainer.eClass().getEAllReferences(), new Predicate<EReference>() {
                @Override
                public boolean apply(EReference ref) {
                    return ref.getEType().equals(obj.eClass()) && ref.getEContainingClass().isAbstract();
                }
            }));

            if (!references.isEmpty()) {
                EReference eOpposite=references.get(0).getEOpposite();
                DBObjectImpl container=(DBObjectImpl) obj.eContainer();
                values.setProperty(INTERNAL_CLASS.apply(eOpposite), Integer.toString(cdoInternalClass(container.eClass())));
                values.setProperty(INTERNAL_CLASS_NAME.apply(eOpposite), DBQueryUtil.quote(cdoInternalClassName(container.eClass())));
            }
        }
        return values;
    }

    private static String cdoInternalClassName(EClass eClass) {
        return eClass.getName();
    }

    public static int cdoInternalClass(EClass eClass) {
        return eClass.getName().hashCode();
    }

    public static void delete(Connection connection, DBObject obj) throws SQLException {
        Statement stmt=connection.createStatement();
        try {
            stmt.execute("DELETE FROM " + DBQueryUtil.getTableName(obj.eClass()) + " WHERE " + CDODBSchema.ATTRIBUTES_ID + '=' + obj.cdoID());

            // On supprime les enfants
            if (!obj.eClass().getEAllContainments().isEmpty()) {
                for (EReference reference : obj.eClass().getEAllContainments()) {
                    if (reference.getUpperBound() == 1) {
                        if(obj.eGet(reference) != null)
                            delete(connection, (DBObject) obj.eGet(reference));
                    } else {
                        for (DBObject child : (List<DBObject>) obj.eGet(reference)) {
                            delete(connection, child);
                        }
                    }
                }
            }

            // On informe le parent
            // FIXME CME awfull
            if (obj.eContainmentFeature() instanceof EReference) {
                // obj.eSet(obj.eContainmentFeature(), null);
                EReference reference=obj.eContainmentFeature();
                if (reference.getUpperBound() == 1 && reference.getEOpposite() != null && obj.eContainer() != null) {
                    DBObjectImpl container=(DBObjectImpl) obj.eContainer();
                    container.map().remove(reference.getEOpposite());
                }
            }

            // On desactive l objet
            objects.invalidate(key(obj));
            ((DBObjectImpl) obj).setCdoID(-1);
            ((DBObjectImpl) obj).setConnection(null);
            // ((DBObjectImpl) obj).map().clear();
            ((DBObjectImpl) obj).cdoSetResource(null);
            ((DBObjectImpl) obj).setRevision(-1L);

            connection.commit();
        } finally {
            stmt.close();
        }
        
    }

    public static void safeExecute(Connection connection, DBRunnable runnable) {
        try {
            execute(connection, runnable);
        } catch (SQLException e) {
            Activator.log(IStatus.ERROR, "SQL error", e); //$NON-NLS-1$
            throw new RemoteException(e);
        }
    }

    public static void execute(Connection connection, DBRunnable runnable) throws SQLException {
        try {
        	synchronized (connection) {
        		canSave.set(true);
                runnable.connection=connection;
                runnable.run();
        		connection.commit();
			}
        } catch (RuntimeException t) {
            connection.rollback();
            throw t;
        } catch (SQLException t) {
            connection.rollback();
            throw t;
        } finally {
        	for (Iterator<Runnable> iterator=post.get().iterator(); iterator.hasNext();) {
        		try {
        			Runnable next=iterator.next();
                    next.run();
        		} catch(Throwable t) {
                    Activator.log(IStatus.ERROR, "Internal error", t); //$NON-NLS-1$
        		}
        		iterator.remove();
        	}
            canSave.set(false);
        }
    }

    public static boolean equals(DBObject obj1, DBObject obj2) {
        if ((obj1 == null) || (obj2 == null))
            return false;

        if (DBUtil.isStoredInMemory(obj1))
            return obj1 == obj2;

        if (obj1.eClass().getClassifierID() != obj2.eClass().getClassifierID())
            return false;

        if (obj1.cdoID() != ((DBObjectImpl) obj2).cdoID())
            return false;

        return true;
    }

    public static boolean isStoredInDB(DBObject object) {
        return object.cdoID() != -1;
    }

    public static boolean isStoredInMemory(DBObject object) {
        return !isStoredInDB(object);
    }

    public static String internalClass(EReference ref) {
        return INTERNAL_CLASS.apply(ref);
    }

    @Deprecated
    public static Cache<Long, DBObject> getCache() {
        return objects;
    }
}
