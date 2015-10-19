package com.kms.katalon.composer.webui.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebDriverPreferenceComposite extends DriverPreferenceComposite {
    private Text txtRemoteServerUrl;
    private Combo cmbRemoteServerType;

    public RemoteWebDriverPreferenceComposite(Composite parent, int style,
            RemoteWebDriverConnector remoteDriverConnector) {
        super(parent, style, remoteDriverConnector);
    }

    @Override
    protected void createContents(final IDriverConnector driverConnector) {
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite remoteUrlComposite = new Composite(this, SWT.NONE);
        remoteUrlComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        remoteUrlComposite.setLayout(new GridLayout(2, false));

        Label lblRemoteServerUrl = new Label(remoteUrlComposite, SWT.NONE);
        lblRemoteServerUrl.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblRemoteServerUrl.setText(StringConstants.LBL_REMOTE_SERVER_URL);

        txtRemoteServerUrl = new Text(remoteUrlComposite, SWT.BORDER);
        txtRemoteServerUrl.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtRemoteServerUrl.setText(((RemoteWebDriverConnector) driverConnector).getRemoteServerUrl());
        
        Composite remoteTypeComposite = new Composite(this, SWT.NONE);
        remoteTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        remoteTypeComposite.setLayout(new GridLayout(2, false));

        Label lblRemoteServerType = new Label(remoteTypeComposite, SWT.NONE);
        lblRemoteServerType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblRemoteServerType.setText(StringConstants.LBL_REMOTE_SERVER_TYPE);
        
        cmbRemoteServerType = new Combo(remoteTypeComposite, SWT.READ_ONLY);
        final String[] stringValues = RemoteWebDriverConnectorType.stringValues();
        int selectedIndex = 0;
        for (int i = 0; i < stringValues.length; i++) {
            if (stringValues[i].equals(((RemoteWebDriverConnector) driverConnector).getRemoteWebDriverConnectorType()
                    .name())) {
                selectedIndex = i;
                break;
            }
        }
        cmbRemoteServerType.setItems(RemoteWebDriverConnectorType.stringValues());
        cmbRemoteServerType.select(selectedIndex);

        driverPropertyMapComposite = new DriverPropertyMapComposite(this);
        driverPropertyMapComposite.setInput(driverConnector.getDriverProperties());

        txtRemoteServerUrl.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                ((RemoteWebDriverConnector) driverConnector).setRemoteServerUrl(txtRemoteServerUrl.getText());
            }
        });

        cmbRemoteServerType.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                int selectedIndex = cmbRemoteServerType.getSelectionIndex();
                ((RemoteWebDriverConnector) driverConnector)
                        .setRemoteWebDriverConnectorType(RemoteWebDriverConnectorType
                                .valueOf(stringValues[selectedIndex]));
            }
        });
    }

    @Override
    public IDriverConnector getResult() {
        return driverConnector;
    }
}
