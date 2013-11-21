package org.eclipse.emf.db;

import org.eclipse.emf.ecore.EObject;

public interface DBObject extends EObject {
	public DBConnection dbConnection();
}
