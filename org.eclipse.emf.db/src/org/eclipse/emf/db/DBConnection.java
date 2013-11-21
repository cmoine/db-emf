package org.eclipse.emf.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.db.criteria.ISearchCriteria;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.internal.db.DBObjectImpl;

public class DBConnection {
	private final Connection con;
	private final EFactory factory;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			Activator.log(IStatus.ERROR, "Failed to load JDBC Driver", e); //$NON-NLS-1$
		}
	}

	public DBConnection(EFactory factory, String url, String user, String pwd) throws SQLException {
		this.factory=factory;
		con=DriverManager.getConnection(url, user, pwd);
	}

	public <T extends DBObject> List<T> getAll(Class<T> c) throws SQLException {
		return getAll(c, null);
	}

	public <T extends DBObject> List<T> getAll(Class<T> c, ISearchCriteria criteria) throws SQLException {
		Statement statement=con.createStatement();
		List<T> result=new ArrayList<T>();
		try {
			StringBuffer strBuf=new StringBuffer("SELECT * FROM "); //$NON-NLS-1$
			strBuf.append(c.getSimpleName());
			if (criteria != null) {
				strBuf.append(" WHERE "); //$NON-NLS-1$
				strBuf.append(criteria.getExpression(null));
			}
			strBuf.append(";"); //$NON-NLS-1$
            Activator.log(IStatus.INFO, "Execute query: " + strBuf, null); //$NON-NLS-1$
			ResultSet resultSet=statement.executeQuery(strBuf.toString());
			EClass clazz=(EClass) factory.getEPackage().getEClassifier(c.getSimpleName());
			while (resultSet.next()) {
				T obj=(T) factory.create(clazz);
				result.add(obj);
				((DBObjectImpl) obj).internalSetDbConnection(this);
				ResultSetMetaData metaData = resultSet.getMetaData();
				List<String> columnNames=new ArrayList<String>();
				for(int i=1; i <= metaData.getColumnCount() ; i++) {
					columnNames.add(metaData.getColumnName(i));
				}
				for (EAttribute att : clazz.getEAllAttributes()) {
					Object value=null;
					if(columnNames.contains(att.getName())) {
						if (att.getEType().equals(EcorePackage.eINSTANCE.getEString()))
							value=resultSet.getString(att.getName());
						else if (att.getEType().equals(EcorePackage.eINSTANCE.getEBoolean()))
							value=resultSet.getBoolean(att.getName());
						else if (att.getEType().equals(EcorePackage.eINSTANCE.getEInt()))
							value=resultSet.getInt(att.getName());
						else if (att.getEType().equals(EcorePackage.eINSTANCE.getEFloat()))
							value=resultSet.getFloat(att.getName());
						obj.eSet(att, value);
					}
				}
			}
		} finally {
			statement.close();
		}
		return result;
	}

	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			Activator.log(IStatus.ERROR, "Failed closing DB connection", e); //$NON-NLS-1$
		}
	}
}
