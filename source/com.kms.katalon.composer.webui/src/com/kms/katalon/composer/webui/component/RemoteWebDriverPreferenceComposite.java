package com.kms.katalon.composer.webui.component;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.kms.katalon.composer.execution.components.DriverPreferenceComposite;
import com.kms.katalon.composer.execution.components.DriverPropertyMapComposite;
import com.kms.katalon.composer.webui.constants.StringConstants;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.configuration.IDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteWebDriverPreferenceComposite extends DriverPreferenceComposite {
    private Text txtRemoteServerUrl;

    private Combo cmbRemoteServerType;

    private Combo cbbAppiumDriver;

    private GridData gdMobileComposite;

    private GridData gdLblDriverType;
    
    private static final String[] MOBILE_DRIVER_NAMES;
    
    private static final String[] REMOTE_SERVER_TYPES;
    
    static {
        MOBILE_DRIVER_NAMES = AppiumMobileDriver.driverNames().toArray(new String[0]);
        REMOTE_SERVER_TYPES = RemoteWebDriverConnectorType.stringValues();
    }

    private static enum AppiumMobileDriver {
        ANDROID_DRIVER(MobileDriverType.ANDROID_DRIVER, "Android Driver"),
        IOS_DRIVER(MobileDriverType.IOS_DRIVER, "iOS Driver");

        private String driverName;

        private MobileDriverType mobileDriver;

        private AppiumMobileDriver(MobileDriverType mobileDriver, String driverName) {
            this.mobileDriver = mobileDriver;
            this.driverName = driverName;
        }

        public static List<String> driverNames() {
            List<String> names = new ArrayList<>();
            for (AppiumMobileDriver driver : values()) {
                names.add(driver.driverName);
            }
            return names;
        }

        public static MobileDriverType fromDriverName(String driverName) {
            for (AppiumMobileDriver driver : values()) {
                if (driver.driverName.equals(driverName)) {
                    return driver.mobileDriver;
                }
            }
            return MobileDriverType.ANDROID_DRIVER;
        }

        public static AppiumMobileDriver fromDriver(MobileDriverType driverType) {
            for (AppiumMobileDriver driver : values()) {
                if (driver.mobileDriver == driverType) {
                    return driver;
                }
            }
            return ANDROID_DRIVER;
        }
    }

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
        GridLayout glRemoteComposite = new GridLayout(2, false);
        glRemoteComposite.verticalSpacing = 10;
        glRemoteComposite.horizontalSpacing = 15;
        remoteUrlComposite.setLayout(glRemoteComposite);

        Label lblRemoteServerUrl = new Label(remoteUrlComposite, SWT.NONE);
        GridData gdLblServerUrl = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblRemoteServerUrl.setLayoutData(gdLblServerUrl);
        lblRemoteServerUrl.setText(StringConstants.LBL_REMOTE_SERVER_URL);

        txtRemoteServerUrl = new Text(remoteUrlComposite, SWT.BORDER);
        GridData gdTxtUrl = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        txtRemoteServerUrl.setLayoutData(gdTxtUrl);
        gdTxtUrl.widthHint = 250;
        txtRemoteServerUrl.setText(((RemoteWebDriverConnector) driverConnector).getRemoteServerUrl());
        txtRemoteServerUrl.setToolTipText(txtRemoteServerUrl.getText());

        txtRemoteServerUrl.addListener(SWT.Resize, new Listener() {

            @Override
            public void handleEvent(Event event) {
                gdTxtUrl.widthHint = event.width;
                gdLblDriverType.widthHint = lblRemoteServerUrl.getBounds().width;
            }
        });

        txtRemoteServerUrl.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                txtRemoteServerUrl.setToolTipText(txtRemoteServerUrl.getText());
            }
        });

        Label lblRemoteServerType = new Label(remoteUrlComposite, SWT.NONE);
        GridData gdLblRemoteServerType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblRemoteServerType.setLayoutData(gdLblRemoteServerType);
        lblRemoteServerType.setText(StringConstants.LBL_REMOTE_SERVER_TYPE);

        cmbRemoteServerType = new Combo(remoteUrlComposite, SWT.READ_ONLY);
        cmbRemoteServerType.setItems(REMOTE_SERVER_TYPES);

        Composite mobileDriverComposite = new Composite(this, SWT.NONE);
        gdMobileComposite = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        mobileDriverComposite.setLayoutData(gdMobileComposite);
        GridLayout glMobileComposite = new GridLayout(2, false);
        glMobileComposite.verticalSpacing = 10;
        glMobileComposite.horizontalSpacing = 15;
        mobileDriverComposite.setLayout(glMobileComposite);

        Label lblDriverType = new Label(mobileDriverComposite, SWT.NONE);
        gdLblDriverType = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
        lblDriverType.setLayoutData(lblDriverType);
        lblDriverType.setLayoutData(gdLblDriverType);
        lblDriverType.setText("Appium driver");

        cbbAppiumDriver = new Combo(mobileDriverComposite, SWT.READ_ONLY);
        final String[] mobileDriverStringValues = MOBILE_DRIVER_NAMES;
        cbbAppiumDriver.setItems(mobileDriverStringValues);

        driverPropertyMapComposite = new DriverPropertyMapComposite(this);
        driverPropertyMapComposite.setInput(driverConnector.getUserConfigProperties());

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
                RemoteWebDriverConnectorType serverDriverType = RemoteWebDriverConnectorType
                        .valueOf(REMOTE_SERVER_TYPES[selectedIndex]);
                ((RemoteWebDriverConnector) driverConnector).setRemoteWebDriverConnectorType(serverDriverType);
                
                updateMobileCompositeLayout(serverDriverType);
            }
        });

        cbbAppiumDriver.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                int selectedIndex = cbbAppiumDriver.getSelectionIndex();
                ((RemoteWebDriverConnector) driverConnector).setMobileDriverType(
                        AppiumMobileDriver.fromDriverName(mobileDriverStringValues[selectedIndex]));
            }
        });
        
        setInput();
    }

    private void setInput() {
        int mobileDriverSelectionIndex = 0;
        for (int i = 0; i < MOBILE_DRIVER_NAMES.length; i++) {
            MobileDriverType mobileDriverType = ((RemoteWebDriverConnector) driverConnector).getMobileDriverType();
            if (mobileDriverType != null
                    && MOBILE_DRIVER_NAMES[i].equals(AppiumMobileDriver.fromDriver(mobileDriverType).driverName)) {
                mobileDriverSelectionIndex = i;
                break;
            }
        }
        cbbAppiumDriver.select(mobileDriverSelectionIndex);
        

        int selectedIndex = 0;
        for (int i = 0; i < REMOTE_SERVER_TYPES.length; i++) {
            if (REMOTE_SERVER_TYPES[i]
                    .equals(((RemoteWebDriverConnector) driverConnector).getRemoteWebDriverConnectorType().name())) {
                selectedIndex = i;
                break;
            }
        }

        cmbRemoteServerType.select(selectedIndex);
        cmbRemoteServerType.notifyListeners(SWT.Selection, new Event());
    }

    private void updateMobileCompositeLayout(RemoteWebDriverConnectorType serverDriverType) {
        gdMobileComposite.heightHint = (serverDriverType != RemoteWebDriverConnectorType.Appium) ? 0 : -1;
        getParent().getParent().layout(true, true);
    }

    @Override
    public IDriverConnector getResult() {
        return driverConnector;
    }
}
