package com.kms.katalon.composer.mobile.objectspy.components;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.swt.widgets.Composite;

import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileAppDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileInspectorController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;

public interface MobileAppComposite {

    Composite createComposite(Composite parent, int type, MobileAppDialog parentDialog);

    boolean validateSetting();

    MobileDriverType getSelectedDriverType();

    boolean startApp(MobileInspectorController controller, AppiumMonitorDialog progressDialog)
            throws InvocationTargetException, InterruptedException;
    
    void setInput() throws InvocationTargetException, InterruptedException;

    String getAppFile();

    String getAppName();
    
    MobileActionMapping buildStartAppActionMapping();

    boolean isAbleToStart();

    void loadDevices() throws InvocationTargetException, InterruptedException;
}
