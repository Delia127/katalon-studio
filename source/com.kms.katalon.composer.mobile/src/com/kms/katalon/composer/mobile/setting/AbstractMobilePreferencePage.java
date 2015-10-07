package com.kms.katalon.composer.mobile.setting;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.composer.execution.settings.DriverPreferencePage;
import com.kms.katalon.composer.mobile.component.DeviceSelectionComposite;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.driver.AbstractMobileDriverConnector;

public abstract class AbstractMobilePreferencePage extends DriverPreferencePage {
    public AbstractMobilePreferencePage() {
    }

    private DeviceSelectionComposite deviceSelectionComposite;
    private AbstractMobileDriverConnector abstractMobileDriverConnector;

    @Override
    protected void initilize() {
        super.initilize();
        abstractMobileDriverConnector = (AbstractMobileDriverConnector) driverConnector;
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite container = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
        layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
        layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
        layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
        container.setLayout(layout);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite deviceSelectionCompositeContainer = new Composite(container, SWT.NONE);
        deviceSelectionCompositeContainer.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        deviceSelectionCompositeContainer.setLayout(new GridLayout());
        deviceSelectionComposite = new DeviceSelectionComposite(deviceSelectionCompositeContainer, SWT.NONE,
                (MobileDriverType) driverConnector.getDriverType());

        deviceSelectionComposite.setDeviceName(abstractMobileDriverConnector.getDeviceName());
        DriverPropertyMapComposite driverPropertyMap = new DriverPropertyMapComposite(container);

        initilize();
        driverPropertyMap.setInput(driverProperties);
        return container;
    }

    @Override
    public boolean performOk() {
        if (abstractMobileDriverConnector != null && deviceSelectionComposite != null) {
            abstractMobileDriverConnector.setDeviceName(deviceSelectionComposite.getDeviceName());
        }
        return super.performOk();
    }
}
