package org.eclipse.emf.cdo.server.internal.db;

public interface CDODBSchema {
    /**
     * Field names of attribute tables
     */
    public static final String ATTRIBUTES_ID="cdo_id"; //$NON-NLS-1$

    public static final String ATTRIBUTES_BRANCH="cdo_branch"; //$NON-NLS-1$

    public static final String ATTRIBUTES_VERSION="cdo_version"; //$NON-NLS-1$

    public static final String ATTRIBUTES_CLASS="cdo_class"; //$NON-NLS-1$

    public static final String ATTRIBUTES_CREATED="cdo_created"; //$NON-NLS-1$

    public static final String ATTRIBUTES_REVISED="cdo_revised"; //$NON-NLS-1$

    public static final String ATTRIBUTES_RESOURCE="cdo_resource"; //$NON-NLS-1$

    public static final String ATTRIBUTES_CONTAINER="cdo_container"; //$NON-NLS-1$

    public static final String ATTRIBUTES_FEATURE="cdo_feature"; //$NON-NLS-1$

    /**
     * Field names of list tables
     */
    public static final String LIST_FEATURE="cdo_feature"; //$NON-NLS-1$

    public static final String LIST_REVISION_ID="cdo_source"; //$NON-NLS-1$

    public static final String LIST_REVISION_VERSION="cdo_version"; //$NON-NLS-1$

    public static final String LIST_REVISION_VERSION_ADDED="cdo_version_added"; //$NON-NLS-1$

    public static final String LIST_REVISION_VERSION_REMOVED="cdo_version_removed"; //$NON-NLS-1$

    public static final String LIST_REVISION_BRANCH="cdo_branch"; //$NON-NLS-1$

    public static final String LIST_IDX="cdo_idx"; //$NON-NLS-1$

    public static final String LIST_VALUE="cdo_value"; //$NON-NLS-1$

}
