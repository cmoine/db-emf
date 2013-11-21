package org.eclipse.emf.internal.db;

import org.eclipse.emf.db.DBConnection;
import org.eclipse.emf.ecore.impl.EObjectImpl;

public class DBObjectImpl extends EObjectImpl {
	private DBConnection connection;

	public DBConnection dbConnection() {
		return connection;
	}

	public void internalSetDbConnection(DBConnection connection) {
		this.connection=connection;
	}
}
