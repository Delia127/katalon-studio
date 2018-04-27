package com.kms.katalon.composer.components.impl.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.FrameworkUtil;

public class StatusUtil {
    private StatusUtil() {
        //Disable default constructor
    }
    
    public static IStatus getErrorStatus(Class<?> clazz, Throwable t) {
        return new Status(Status.ERROR, FrameworkUtil.getBundle(clazz).getSymbolicName(), t.getMessage(), t);
    }
    
    public static IStatus getMultiStatus(Class<?> clazz, IStatus[] statuses, String message, Throwable t) {
        return new MultiStatus(
                FrameworkUtil.getBundle(clazz).getSymbolicName(),
                IStatus.OK,
                statuses,
                message,
                t);
    }
}
