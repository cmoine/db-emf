package org.eclipse.emf.importer.db.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.converter.ui.contribution.base.ModelConverterPage;
import org.eclipse.emf.importer.ModelImporter;
import org.eclipse.emf.importer.db.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DBDetailPage extends ModelConverterPage {
    // private Text url;
    // private Text user;
    // private Text pwd;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver"); //$NON-NLS-1$
		} catch (ClassNotFoundException e) {
			Activator.log(IStatus.ERROR, "Failed to load JDBC Driver", e); //$NON-NLS-1$
		}
	}

	public DBDetailPage(ModelImporter modelImporter, String pageName) {
		super(modelImporter, pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite compo=new Composite(parent, SWT.NONE);
        // compo.setLayout(new GridLayout(3, false));
        //		url=createLine(compo, SWT.NONE, "JDBC adress (exemple: jdbc:mysql://localhost:3306/atms) : ", 1); //$NON-NLS-1$
        //		url.setText("jdbc:mysql://localhost:3306/atms"); //$NON-NLS-1$
        // url.selectAll();
        // url.setFocus();
        // Button button=new Button(compo, SWT.NONE);
        //		button.setText("Load"); //$NON-NLS-1$
        // button.addListener(SWT.Selection, this);
        //		user=createLine(compo, SWT.NONE, "User : ", 2); //$NON-NLS-1$
        //		user.setText("mls"); //$NON-NLS-1$
        //		pwd=createLine(compo, SWT.PASSWORD, "Password : ", 2); //$NON-NLS-1$
        //		pwd.setText("ocean"); //$NON-NLS-1$

		setControl(compo);

		setErrorMessage("You must load a valid DB"); //$NON-NLS-1$
	}

	protected Text createLine(Composite parent, int style, String label, int horizontalSpan) {
		new Label(parent, SWT.NONE).setText(label);
		Text text=new Text(parent, SWT.BORDER | style);
		text.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, horizontalSpan, 1));
		return text;
	}

	@Override
	public void handleEvent(Event event) {
        // try {
        // getModelConverter().clearEPackagesCollections();
        // Connection con=DriverManager.getConnection(url.getText(), user.getText(), pwd.getText());
        // EPackage ePackage=EcoreFactory.eINSTANCE.createEPackage();
        //			String prefix=StringUtils.substringAfterLast(url.getText(), "/"); //$NON-NLS-1$
        // ePackage.setName(prefix);
        // ePackage.setNsPrefix(prefix);
        //            ePackage.setNsURI("http://www.eclipse.org/" + prefix + "/1.0.0"); //$NON-NLS-1$ //$NON-NLS-2$
        // getModelConverter().getEPackages().add(ePackage);
        //			for (String[] tableName : list(con, "show full tables where Table_type!='VIEW';", 1)) { //$NON-NLS-1$
        // EClass eClass=EcoreFactory.eINSTANCE.createEClass();
        // eClass.setName(tableName[0]);
        // ePackage.getEClassifiers().add(eClass);
        //				String[][] columnNames=list(con, "show columns from " + tableName[0] + ";", 4); //$NON-NLS-1$ //$NON-NLS-2$
        // // Handle attributes:
        // for (String[] columnName : columnNames) {
        // // if (!StringUtils.equals(columnName[3], "PRI")) {
        // EAttribute att=EcoreFactory.eINSTANCE.createEAttribute();
        // att.setName(columnName[0]);
        // att.setEType(getType(columnName[1]));
        // eClass.getEStructuralFeatures().add(att);
        // // }
        // }
        // }
        // // getModelConverter().setModelFile(file)
        // // if (getModelConverter().getGenModelFileName() != null)
        // // getModelConverter().setGenModelFileName("test.genmodel");
        //
        // setErrorMessage(null);
        // getContainer().updateButtons();
        // } catch (SQLException e) {
        //			Activator.log(IStatus.ERROR, "Failed to refresh", e); //$NON-NLS-1$
        // }
	}

    // protected EClassifier getType(String type) {
    // if (type.startsWith("varchar") || type.startsWith("text") || type.startsWith("longtext"))
    // return EcorePackage.eINSTANCE.getEString();
    // if (type.startsWith("int"))
    // return EcorePackage.eINSTANCE.getEInt();
    // if (type.startsWith("smallint"))
    // return EcorePackage.eINSTANCE.getEShort();
    // if (type.startsWith("float"))
    // return EcorePackage.eINSTANCE.getEFloat();
    // if (type.startsWith("double"))
    // return EcorePackage.eINSTANCE.getEDouble();
    // if (type.startsWith("decimal"))
    // return EcorePackage.eINSTANCE.getEBigDecimal();
    // if (type.startsWith("bigint"))
    // return EcorePackage.eINSTANCE.getEBigInteger();
    // if (type.contains("blob"))
    // return EcorePackage.eINSTANCE.getEByteArray();
    // if (type.contains("date"))
    // return EcorePackage.eINSTANCE.getEDate();
    // if (type.contains("bit"))
    // return EcorePackage.eINSTANCE.getEBoolean();
    //
    // System.out.println("DBDetailPage.getType()");
    // return null;
    // }

	@Override
	protected DBModelImporter getModelConverter() {
		return (DBModelImporter) super.getModelConverter();
	}

    // private String[][] list(Connection con, String sqlQuery, int columnNumber) throws SQLException {
    // List<String[]> result=new ArrayList<String[]>();
    // ResultSet resultSet=con.createStatement().executeQuery(sqlQuery);
    // while (resultSet.next()) {
    // String[] columnName=new String[columnNumber];
    // for (int i=0; i < columnNumber; i++)
    // columnName[i]=resultSet.getString(i + 1);
    // result.add(columnName);
    // }
    // resultSet.getStatement().close();
    // return result.toArray(new String[][] {});
    // }

	// @Override
	// public boolean canFlipToNextPage() {
	// return true;
	// }
}
