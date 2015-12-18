package com.kms.katalon.composer.components.impl.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.FrameworkUtil;

public class StatusUtil {
    private StatusUtil() {
        //Disable default constructor
    }
    
    public static IStatus getErrorStatus(Class<?> clazz, Throwable t) {
        return new Status(Status.ERROR, FrameworkUtil.getBundle(clazz).getSymbolicName(), t.getMessage(), t);
    }
}
