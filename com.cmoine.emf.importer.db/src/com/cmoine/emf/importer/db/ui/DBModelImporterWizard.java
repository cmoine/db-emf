package com.cmoine.emf.importer.db.ui;

import org.eclipse.emf.converter.ModelConverter;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterWizard;

public class DBModelImporterWizard extends ModelImporterWizard {

	@Override
	protected ModelConverter createModelConverter() {
		return new DBModelImporter();
	}

	@Override
	public void addPages() {
		DBDetailPage detailPage=new DBDetailPage(getModelImporter(), "DBModel"); //$NON-NLS-1$
		addPage(detailPage);

		DBPackagePage packagePage=new DBPackagePage(getModelImporter(), "DBPackages"); //$NON-NLS-1$
		packagePage.setShowReferencedGenModels(true);
		addPage(packagePage);
	}
}
