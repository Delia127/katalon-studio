package com.kms.katalon.composer.webui.component;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;

public class RemoteWebDriverPreferenceComposite extends DriverPreferenceComposite {
    private Text txtRemoteServerUrl;
    
    public RemoteWebDriverPreferenceComposite(Composite parent, int style, RemoteWebDriverConnector remoteDriverConnector) {
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
        
        driverPropertyMapComposite = new DriverPropertyMapComposite(this);
        
        txtRemoteServerUrl.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                ((RemoteWebDriverConnector) driverConnector).setRemoteServerUrl(txtRemoteServerUrl.getText());
            }
        });
    }

    @Override
    public IDriverConnector getResult() {
        return driverConnector;
    }
}
