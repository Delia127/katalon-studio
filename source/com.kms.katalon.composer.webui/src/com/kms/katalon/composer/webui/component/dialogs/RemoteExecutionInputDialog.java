package com.kms.katalon.composer.webui.component.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.webui.constants.ComposerWebuiMessageConstants;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteExecutionInputDialog extends Dialog {
    private RemoteWebDriverConnectorType removeDriverType;

    private String remoteServerUrl;

    public RemoteExecutionInputDialog(Shell parentShell, String remoteServerUrl,
            RemoteWebDriverConnectorType removeDriverType) {
        super(parentShell);
        this.remoteServerUrl = remoteServerUrl;
        this.removeDriverType = removeDriverType;
        if (this.removeDriverType == null) {
            this.removeDriverType =  RemoteWebDriverConnectorType.Selenium;
        }
    }

    public RemoteWebDriverConnectorType getRemoveDriverType() {
        return removeDriverType;
    }

    public String getRemoteServerUrl() {
        return remoteServerUrl;
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        createTextComposite(composite);
        createComboComposite(composite);
        return composite;
    }

    public void createComboComposite(Composite composite) {
        Composite comboComposite = new Composite(composite, SWT.NONE);
        comboComposite.setLayout(new GridLayout(2, false));
        comboComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label lblRemoteType = new Label(comboComposite, SWT.NONE);
        lblRemoteType.setText(ComposerWebuiMessageConstants.LBL_DLG_REMOTE_DRIVER_TYPE);
        final Combo combo = new Combo(comboComposite, SWT.READ_ONLY);
        combo.setItems(RemoteWebDriverConnectorType.stringValues());
        int indexOfRemoteType = RemoteWebDriverConnectorType.indexOf(removeDriverType);
        combo.select(indexOfRemoteType == -1 ? 0 : indexOfRemoteType);
        combo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                removeDriverType = RemoteWebDriverConnectorType.valueOf(combo.getText());
            }
        });
    }

    public void createTextComposite(Composite composite) {
        Composite textComposite = new Composite(composite, SWT.NONE);
        textComposite.setLayout(new GridLayout(2, false));
        textComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Label lblRemoteServer = new Label(textComposite, SWT.NONE);
        lblRemoteServer.setText(ComposerWebuiMessageConstants.LBL_DLG_REMOTE_SERVER_URL);
        final Text txtServerUrl = new Text(textComposite, SWT.SINGLE | SWT.BORDER);
        txtServerUrl.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
        txtServerUrl.setText(remoteServerUrl != null ? remoteServerUrl : ""); //$NON-NLS-1$
        txtServerUrl.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                remoteServerUrl = txtServerUrl.getText();
                getButton(OK).setEnabled(!remoteServerUrl.isEmpty());
            }
        });
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(OK).setEnabled(remoteServerUrl != null ? !remoteServerUrl.isEmpty() : false);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(500, 200);
    }

    @Override
    protected void configureShell(Shell shell) {
        super.configureShell(shell);
        shell.setText(StringConstants.DIA_REMOTE_SERVER_URL_TITLE);
    }
}
