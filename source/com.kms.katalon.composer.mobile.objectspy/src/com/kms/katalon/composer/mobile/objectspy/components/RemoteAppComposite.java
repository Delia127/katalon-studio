package com.kms.katalon.composer.mobile.objectspy.components;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.kms.katalon.composer.components.impl.dialogs.MultiStatusErrorDialog;
import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileAction;
import com.kms.katalon.composer.mobile.objectspy.actions.MobileActionMapping;
import com.kms.katalon.composer.mobile.objectspy.constant.StringConstants;
import com.kms.katalon.composer.mobile.objectspy.dialog.AppiumMonitorDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileAppDialog;
import com.kms.katalon.composer.mobile.objectspy.dialog.MobileInspectorController;
import com.kms.katalon.composer.project.handlers.SettingHandler;
import com.kms.katalon.composer.testcase.groovy.ast.expressions.ConstantExpressionWrapper;
import com.kms.katalon.constants.IdConstants;
import com.kms.katalon.controller.ProjectController;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.core.mobile.keyword.internal.MobileDriverFactory;
import com.kms.katalon.execution.webui.configuration.RemoteWebRunConfiguration;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteAppComposite implements MobileAppComposite {

    private RemoteWebRunConfiguration runConfiguration;

    private Label lblConfigurationDetails;

    private Text txtApplicationId;

    private Button btnEditConfigurationDetails;
    
    private MobileAppDialog parentDialog;
    
    public RemoteAppComposite() {
    }

    @Override
    public Composite createComposite(Composite parent, int type, MobileAppDialog parentDialog) {
        this.parentDialog = parentDialog;
        Composite container = new Composite(parent, type);
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.horizontalSpacing = 15;
        container.setLayout(glContainer);

        Label lblConfigurationData = new Label(container, SWT.NONE);
        lblConfigurationData.setText("Configuration");

        Composite compositeConfigurationData = new Composite(container, SWT.NONE);
        compositeConfigurationData.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        GridLayout glCompositeConfigurationData = new GridLayout(2, false);
        glCompositeConfigurationData.marginWidth = 0;
        glCompositeConfigurationData.marginHeight = 0;
        compositeConfigurationData.setLayout(glCompositeConfigurationData);

        lblConfigurationDetails = new Label(compositeConfigurationData, SWT.NONE);
        lblConfigurationDetails.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        btnEditConfigurationDetails = new Button(compositeConfigurationData, SWT.PUSH);
        btnEditConfigurationDetails.setText("Edit");

        Label lblApplicationId = new Label(container, SWT.NONE);
        lblApplicationId.setText("Cloud Application ID");

        txtApplicationId = new Text(container, SWT.BORDER);
        txtApplicationId.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        return container;
    }

    @Override
    public boolean validateSetting() {
        return true;
    }

    @Override
    public MobileDriverType getSelectedDriverType() {
        if (runConfiguration == null || runConfiguration.getRemoteDriverConnector() == null) {
            return MobileDriverType.ANDROID_DRIVER;
        }
        RemoteWebDriverConnector remoteDriverConnector = runConfiguration.getRemoteDriverConnector();
        return remoteDriverConnector.getMobileDriverType();
    }

    @Override
    public boolean startApp(MobileInspectorController inspectorController, AppiumMonitorDialog progressDialog)
            throws InvocationTargetException, InterruptedException {
        final String applicationId = txtApplicationId.getText();
        IRunnableWithProgress processToRun = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                monitor.beginTask(StringConstants.DIA_LBL_STATUS_APP_STARTING, IProgressMonitor.UNKNOWN);

                progressDialog.runAndWait(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        inspectorController.startMobileAppOnCloudDevices(runConfiguration, applicationId);
                        return null;
                    }
                });
                checkMonitorCanceled(monitor);

                monitor.done();
            }
        };

        progressDialog.run(true, true, processToRun);
        return true;
    }
    
    private void checkMonitorCanceled(IProgressMonitor monitor) throws InterruptedException {
        if (monitor.isCanceled()) {
            throw new InterruptedException(StringConstants.DIA_ERROR_MSG_OPERATION_CANCELED);
        }
    }

    @Override
    public void setInput() throws InvocationTargetException, InterruptedException {
        try {
            updateRunConfigurationDetails();
        } catch (IOException ex) {
            throw new InvocationTargetException(ex);
        }

        btnEditConfigurationDetails.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                SettingHandler settingHander = SettingHandler.getInstance();
                settingHander.openSettingsPage(IdConstants.SETTING_CAPABILITIES_REMOTE);
                try {
                    updateRunConfigurationDetails();
                    parentDialog.refreshButtonsState();
                } catch (IOException ex) {
                    MultiStatusErrorDialog.showErrorDialog(ex, StringConstants.ERROR,
                            "Unable to reload Remote configuration");
                    LoggerSingleton.logError(ex);
                }
            }
        });
        
        txtApplicationId.addModifyListener(new ModifyListener() {
            
            @Override
            public void modifyText(ModifyEvent e) {
                parentDialog.refreshButtonsState();
            }
        });
    }

    private void updateRunConfigurationDetails() throws IOException {
        runConfiguration = new RemoteWebRunConfiguration(
                ProjectController.getInstance().getCurrentProject().getFolderLocation());
        lblConfigurationDetails.setText(runConfiguration.getName());
    }

    @Override
    public String getAppFile() {
        return txtApplicationId.getText();
    }

    @Override
    public String getAppName() {
        return txtApplicationId.getText();
    }

    @Override
    public MobileActionMapping buildStartAppActionMapping() {
        MobileActionMapping startAppAction = new MobileActionMapping(MobileAction.StartApplication, null);
        String appValue = getAppFile();
        startAppAction.getData()[0].setValue(new ConstantExpressionWrapper(appValue));
        return startAppAction;
    }

    @Override
    public boolean isAbleToStart() {
        return StringUtils.isNotEmpty(txtApplicationId.getText());
    }

    @Override
    public void loadDevices() throws InvocationTargetException, InterruptedException {

    }

}
