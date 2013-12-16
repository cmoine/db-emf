package org.eclipse.emf.importer.db.ui;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.importer.ModelImporter;

public class DBModelImporter extends ModelImporter {
    // private static final String PLUGIN_VARIABLE="CDO=org.eclipse.emf.cdo";
    private static final String ROOT_EXTENDS_INTERFACE="org.eclipse.emf.db.DBObject"; //$NON-NLS-1$
    private static final String ROOT_EXTENDS_CLASS="org.eclipse.emf.db.impl.DBObjectImpl"; //$NON-NLS-1$

	@Override
	public String getID() {
		return "com.cmoine.emf.importer.db"; //$NON-NLS-1$
	}

	@Override
	protected void adjustGenModel(Monitor monitor) {
		super.adjustGenModel(monitor);

		GenModel genModel=getGenModel();

		if (!genModel.getFeatureDelegation().equals(GenDelegationKind.REFLECTIVE_LITERAL)) {
			genModel.setFeatureDelegation(GenDelegationKind.REFLECTIVE_LITERAL);
		}

		if (!ROOT_EXTENDS_CLASS.equals(genModel.getRootExtendsClass())) {
			genModel.setRootExtendsClass(ROOT_EXTENDS_CLASS);
		}

		if (!ROOT_EXTENDS_INTERFACE.equals(genModel.getRootExtendsInterface())) {
			genModel.setRootExtendsInterface(ROOT_EXTENDS_INTERFACE);
		}

        // EList<String> pluginVariables=genModel.getModelPluginVariables();
        // if (!pluginVariables.contains(PLUGIN_VARIABLE)) {
        // pluginVariables.add(PLUGIN_VARIABLE);
        // }
	}
}
