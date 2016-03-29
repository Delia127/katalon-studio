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
import com.kms.katalon.execution.webui.driver.RemoteDebugDriverConnector;
import com.kms.katalon.execution.webui.driver.WebUiDriverConnector;

public class RemoteDebugPreferenceComposite extends DriverPreferenceComposite {
	
    protected Text txtDebugPort;

    public RemoteDebugPreferenceComposite(Composite parent, int style,
    		WebUiDriverConnector driverConnector) {
        super(parent, style, driverConnector);
    }

    @Override
    protected void createContents(final IDriverConnector driverConnector) {
        setLayout(new GridLayout());
        setLayoutData(new GridData(GridData.FILL_BOTH));

        Composite remoteUrlComposite = new Composite(this, SWT.NONE);
        remoteUrlComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        remoteUrlComposite.setLayout(new GridLayout(2, false));

        Label lblRemoteDebugPort = new Label(remoteUrlComposite, SWT.NONE);
        lblRemoteDebugPort.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        lblRemoteDebugPort.setText(StringConstants.LBL_DEBUG_PORT);

        txtDebugPort = new Text(remoteUrlComposite, SWT.BORDER);
        txtDebugPort.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        txtDebugPort.setText(((RemoteDebugDriverConnector) driverConnector).getDebugPort());
        
        Composite remoteTypeComposite = new Composite(this, SWT.NONE);
        remoteTypeComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        remoteTypeComposite.setLayout(new GridLayout(2, false));

        Label desiredCap = new Label(remoteTypeComposite, SWT.NONE);
        desiredCap.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
        desiredCap.setText(StringConstants.LBL_DESIRED_CAP);
        
        driverPropertyMapComposite = new DriverPropertyMapComposite(this);
        driverPropertyMapComposite.setInput(driverConnector.getUserConfigProperties());

        txtDebugPort.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                ((RemoteDebugDriverConnector) driverConnector).setDebugPort(txtDebugPort.getText());
            }
        });

    }

    @Override
    public IDriverConnector getResult() {
        return driverConnector;
    }
}
