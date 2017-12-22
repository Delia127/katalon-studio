package com.kms.katalon.composer.mobile.dialog;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.mobile.component.DeviceSelectionComposite;
import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public class DeviceSelectionDialog extends TitleAreaDialog {
    private MobileDriverType platform;
    private DeviceSelectionComposite deviceSelectionComposite;
    private MobileDeviceInfo device;

    public DeviceSelectionDialog(Shell parentShell, MobileDriverType platform) {
        super(parentShell);
        this.platform = platform;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);
        deviceSelectionComposite = new DeviceSelectionComposite(area, SWT.NONE, platform);
        deviceSelectionComposite.setSelectionIndex(0);
        deviceSelectionComposite.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateStatus();
            }
        });
        
        return area;
    }

    private void updateStatus() {
        super.getButton(OK).setEnabled(deviceSelectionComposite.getSelectedDevice() != null);
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        updateStatus();
    }

    @Override
    public void create() {
        super.create();
        setTitle(platform + " Devices");
        setMessage(StringConstants.DIA_SELECT_DEVICE_NAME_MSG, IMessageProvider.INFORMATION);
    }

    @Override
    protected Point getInitialSize() {
        return getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
    }

    @Override
    protected void okPressed() {
        device = deviceSelectionComposite.getSelectedDevice();
        super.okPressed();
    }

    public MobileDeviceInfo getDevice() {
        return device;
    }
}