package org.eclipse.emf.db.criteria;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.db.util.DBQueryUtil;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

public class StructuralFeatureCriteria implements ISearchCriteria {

    private static final SimpleDateFormat SDF_SECOND=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //$NON-NLS-1$

    private final EStructuralFeature feature;
    private final String value;
    private String prefix=null;
    private String operator="="; //$NON-NLS-1$

    public StructuralFeatureCriteria(EAttribute feature, boolean value) {
        this(feature, value ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
        Assert.isTrue(feature.getEType().equals(EcorePackage.eINSTANCE.getEBoolean()));
    }

    public StructuralFeatureCriteria(EAttribute feature, long value) {
        this.feature=feature;
        this.value=Long.toString(value);
    }

    public StructuralFeatureCriteria(EAttribute feature, Enum e) {
        this(feature, Integer.toString(e.ordinal()));
    }

    public StructuralFeatureCriteria(EAttribute feature, Date date) {
        this.feature=feature;
        this.value=DBQueryUtil.quote(SDF_SECOND.format(date));
    }

    public StructuralFeatureCriteria(EAttribute feature, String value) {
        this.feature=feature;
        this.value=DBQueryUtil.quote(value);
    }

    protected StructuralFeatureCriteria(EReference feature, Collection<String> values) {
        this(feature, values.toArray(new String[values.size()]));
    }

    protected StructuralFeatureCriteria(EReference feature, String[] values) {
        this((EStructuralFeature) feature, values);
    }

    protected StructuralFeatureCriteria(EReference feature, String value) {
        this.feature=feature;
        this.value=value;
    }

    protected StructuralFeatureCriteria(EStructuralFeature feature, Collection<String> values) {
        this(feature, values.toArray(new String[values.size()]));
    }

    protected StructuralFeatureCriteria(EStructuralFeature feature, String[] values) {
        this.feature=feature;
        if (values.length == 1) {
            this.value=DBQueryUtil.quote(values[0]);
        } else {
            operator=" IN "; //$NON-NLS-1$
            String[] newValues=new String[values.length];
            for (int i=0; i < values.length; i++)
                newValues[i]=DBQueryUtil.quote(values[i]);
            this.value="(" + StringUtils.join(newValues, ",") + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    public StructuralFeatureCriteria usePrefix() {
        return usePrefix(feature.getEContainingClass());
    }

    public StructuralFeatureCriteria usePrefix(EClass clazz) {
        this.prefix=DBQueryUtil.getTableName((Class<EObject>) clazz.getInstanceClass());
        return this;
    }

    public StructuralFeatureCriteria usePrefix(String prefix) {
        this.prefix=prefix;
        return this;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public String getExpression(Class<? extends EObject> clazz) {
        check(clazz);
        String feature0=getFeatureName(getFeature());
        String expression=feature0 + " " + getOperator() + " " + getValue(); //$NON-NLS-1$ //$NON-NLS-2$
        if (prefix != null)
            return prefix + "." + expression; //$NON-NLS-1$
        return expression;
    }

    protected String getFeatureName(EStructuralFeature feature) {
        return DBQueryUtil.getColumnName(feature);
    }

    public EStructuralFeature getFeature() {
        return feature;
    }

    public String getValue() {
        return value;
    }

    protected void check(Class<? extends EObject> clazz) {
        Assert.isTrue(clazz == null || feature.getEContainingClass().getInstanceClass().isAssignableFrom(clazz), feature.getEContainingClass()
                .getInstanceClass() + " not assignable from " + clazz); //$NON-NLS-1$
    }
}