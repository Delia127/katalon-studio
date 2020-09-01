package com.kms.katalon.controller;

import org.apache.commons.lang.StringUtils;
import org.eclipse.e4.core.contexts.EclipseContextFactory;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.kms.katalon.dal.setting.IDataProviderSetting;

public abstract class EntityController {
    private static IDataProviderSetting dataProviderSetting;

    protected EntityController() {
    };

    protected static IDataProviderSetting getDataProviderSetting() {
        if (dataProviderSetting == null) {
            initDataProviderSetting();
        }
        return dataProviderSetting;
    }

    private static void initDataProviderSetting() {
        BundleContext bundleContext = FrameworkUtil.getBundle(EntityController.class).getBundleContext();
        IEclipseContext eclipseContext = EclipseContextFactory.getServiceContext(bundleContext);
        dataProviderSetting = eclipseContext.get(IDataProviderSetting.class);
    }
    
    public static String toValidFileName(String fileName) {
        fileName = StringUtils.stripStart(fileName, ".");
        fileName = StringUtils.stripEnd(fileName, ".");
        return fileName.replaceAll("[^A-Za-z-0-9_().\\- ,]", "");
    }
    
    public static String toXmlString(Object entity) throws Exception {
        return getDataProviderSetting().getEntityDataProvider().toXmlString(entity);
    }
}
