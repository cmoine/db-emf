package com.cmoine.emf.importer.db.ui;
import org.eclipse.emf.importer.ModelImporter;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterPackagePage;

public class DBPackagePage extends ModelImporterPackagePage
{
	public DBPackagePage(ModelImporter modelImporter, String pageName) {
		super(modelImporter, pageName);
	}

	@Override
	protected boolean supportsNestedPackages() {
		return true;
	}
}