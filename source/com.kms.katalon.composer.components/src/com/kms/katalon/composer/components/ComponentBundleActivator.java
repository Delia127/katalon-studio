package com.kms.katalon.composer.components;

import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.e4.ui.css.swt.theme.IThemeManager;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@SuppressWarnings("restriction")
public class ComponentBundleActivator extends AbstractUIPlugin {

    private static IThemeManager themeManager;

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        themeManager = getThemeManager(context);
    }

    private IThemeManager getThemeManager(BundleContext context) {
        ServiceReference<IThemeManager> ref = context.getServiceReference(IThemeManager.class);
        return context.getService(ref);
    }
    
    public static IThemeEngine getThemeEngine(Display display) {
        return themeManager.getEngineForDisplay(display);
    }
    
    public static boolean isDarkTheme(Display display) {
        IThemeEngine engine = ComponentBundleActivator.getThemeEngine(display);
        ITheme activeTheme = engine.getActiveTheme();
        return activeTheme.getId().contains("dark");
    }
}
