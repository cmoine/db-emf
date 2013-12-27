package org.eclipse.emf.db.impl;

import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.db.DBObject;
import org.eclipse.emf.ecore.EStructuralFeature;

public class DBNotification extends NotificationImpl {
    public DBNotification(int eventType, DBObject notifier, EStructuralFeature feature, Object oldValue, Object newValue) {
        super(eventType, oldValue, newValue);
    }

}
