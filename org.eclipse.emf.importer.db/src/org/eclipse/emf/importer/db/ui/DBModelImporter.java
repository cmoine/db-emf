package org.eclipse.emf.importer.db.ui;

import java.text.MessageFormat;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenDelegationKind;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.converter.ConverterPlugin;
import org.eclipse.emf.converter.util.ConverterUtil;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.importer.ModelImporter;

public class DBModelImporter extends ModelImporter {
    // private static final String PLUGIN_VARIABLE="CDO=org.eclipse.emf.cdo";
    private static final String ROOT_EXTENDS_INTERFACE="org.eclipse.emf.db.DBObject"; //$NON-NLS-1$
    private static final String ROOT_EXTENDS_CLASS="org.eclipse.emf.db.impl.DBObjectImpl"; //$NON-NLS-1$

	@Override
	public String getID() {
        return "org.eclipse.emf.importer.db"; //$NON-NLS-1$
	}

    @Override
    protected Diagnostic doComputeEPackages(Monitor monitor) throws Exception {
        Diagnostic diagnostic=Diagnostic.OK_INSTANCE;

        List<URI> locationURIs=getModelLocationURIs();
        if (locationURIs.isEmpty()) {
            diagnostic=new BasicDiagnostic(Diagnostic.ERROR, "org.eclipse.emf.cdo.internal.migrator", 0, //$NON-NLS-1$
                    "Specify a valid Ecore model and try loading again", null); //$NON-NLS-1$
        } else {
            monitor.beginTask("", 2); //$NON-NLS-1$
            monitor.subTask(MessageFormat.format("Loading {0}", locationURIs)); //$NON-NLS-1$

            ResourceSet ecoreResourceSet=createResourceSet();
            for (URI ecoreModelLocation : locationURIs) {
                ecoreResourceSet.getResource(ecoreModelLocation, true);
            }

            EcoreUtil.resolveAll(ecoreResourceSet);

            for (Resource resource : ecoreResourceSet.getResources()) {
                getEPackages().addAll(EcoreUtil.<EPackage> getObjectsByType(resource.getContents(), EcorePackage.Literals.EPACKAGE));
            }

            BasicDiagnostic diagnosticChain=new BasicDiagnostic(ConverterPlugin.ID, ConverterUtil.ACTION_MESSAGE_NONE,
                    "Problems were detected while validating and converting the Ecore models", null); //$NON-NLS-1$
            for (EPackage ePackage : getEPackages()) {
                Diagnostician.INSTANCE.validate(ePackage, diagnosticChain);
            }

            if (diagnosticChain.getSeverity() != Diagnostic.OK) {
                diagnostic=diagnosticChain;
            }
        }

        return diagnostic;
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
