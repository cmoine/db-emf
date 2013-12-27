package org.eclipse.emf.importer.db.ui;

import org.eclipse.emf.converter.ModelConverter;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterDetailPage;
import org.eclipse.emf.importer.ui.contribution.base.ModelImporterWizard;

public class DBModelImporterWizard extends ModelImporterWizard {

	@Override
	protected ModelConverter createModelConverter() {
		return new DBModelImporter();
	}

	@Override
	public void addPages() {
        // see CDOImporterWizard
        ModelImporterDetailPage detailPage=new ModelImporterDetailPage(getModelImporter(), "Reload"); //$NON-NLS-1$
        detailPage.setTitle("Ecore Import"); //$NON-NLS-1$
        detailPage.setDescription("Specify one or more '.ecore' or '.emof' URIs and try to load them"); //$NON-NLS-1$
        addPage(detailPage);

		DBPackagePage packagePage=new DBPackagePage(getModelImporter(), "DBPackages"); //$NON-NLS-1$
		packagePage.setShowReferencedGenModels(true);
		addPage(packagePage);
	}
}
