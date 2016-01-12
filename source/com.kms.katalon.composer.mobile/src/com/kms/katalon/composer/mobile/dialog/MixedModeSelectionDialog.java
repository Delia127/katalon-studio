package com.kms.katalon.composer.mobile.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.mobile.constants.StringConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;

public class MixedModeSelectionDialog extends TitleAreaDialog {

    private String mobileDeviceName;

    private Combo cbbDevices;

    private String browserName;

    private Combo cbbBrowsers;

    private Composite container;

    private Map<String, String> devicesList;

    public MixedModeSelectionDialog(Shell parentShell) {
        super(parentShell);
        devicesList = new LinkedHashMap<>();
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite area = (Composite) super.createDialogArea(parent);

        container = new Composite(area, SWT.NONE);
        container.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout glContainer = new GridLayout(2, false);
        glContainer.verticalSpacing = 10;
        container.setLayout(glContainer);

        // Load devices name
        try {
            devicesList.clear();
            getAndroidDevices();
            getIosDevices();
        } catch (Exception ex) {}

        Label theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.DIA_DEVICE_NAME);

        cbbDevices = new Combo(container, SWT.READ_ONLY);
        cbbDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbbDevices.setItems(devicesList.values().toArray(new String[] {}));
        cbbDevices.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateStatus();
            }
        });

        theLabel = new Label(container, SWT.NONE);
        theLabel.setText(StringConstants.DIA_BROWSER_NAME);

        cbbBrowsers = new Combo(container, SWT.READ_ONLY);
        cbbBrowsers.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        cbbBrowsers.setItems(WebUIDriverType.stringValues());
        // cbbBrowsers.select(0);
        cbbBrowsers.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateStatus();
            }
        });

        return area;
    }

    private void updateStatus() {
        if (cbbDevices.getSelectionIndex() < 0 && cbbBrowsers.getSelectionIndex() < 0) {
            super.getButton(OK).setEnabled(false);
        } else {
            super.getButton(OK).setEnabled(true);
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        updateStatus();
    }

    @Override
    public void create() {
        super.create();
        setTitle("Select your Browser, Device");
        setMessage(StringConstants.DIA_SELECT_MIXED_MODE_MSG, IMessageProvider.INFORMATION);
    }

    @Override
    protected Point getInitialSize() {
        return new Point(400, 220);
    }

    @Override
    protected void okPressed() {
        mobileDeviceName = cbbDevices.getText();
        browserName = cbbBrowsers.getText();
        super.okPressed();
    }

    public String getDeviceName() {
        return mobileDeviceName;
    }

    public String getBrowserName() {
        return browserName;
    }

    private void getIosDevices() throws Exception {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            List<String> deviceIds = new ArrayList<String>();
            String[] cmd = { "idevice_id", "-l" };
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process p = pb.start();
            p.waitFor();
            String line = null;
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = br.readLine()) != null) {
                deviceIds.add(line);
            }

            for (String deviceId : deviceIds) {
                cmd = new String[] { "ideviceinfo", "-u", deviceId };
                pb.command(cmd);
                p = pb.start();
                p.waitFor();
                br = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String deviceInfo = "";
                while ((line = br.readLine()) != null) {
                    if (line.contains("DeviceClass:")) {
                        deviceInfo = line.substring("DeviceClass:".length(), line.length()).trim();
                        continue;
                    }
                    if (line.contains("DeviceName:")) {
                        deviceInfo += " " + line.substring("DeviceName:".length(), line.length()).trim();
                        continue;
                    }
                    if (line.contains("ProductVersion:")) {
                        deviceInfo += " " + line.substring("ProductVersion:".length(), line.length()).trim();
                        continue;
                    }
                }

                deviceInfo += " (iOS)";

                devicesList.put(deviceId, deviceInfo);
            }
        }
    }

    private void getAndroidDevices() throws Exception {
        String adbPath = System.getenv("ANDROID_HOME");
        if (adbPath != null) {
            List<String> deviceIds = new ArrayList<String>();
            adbPath += File.separator + "platform-tools" + File.separator + "adb";
            String[] cmd = new String[] { adbPath, "devices" };
            ProcessBuilder pb = new ProcessBuilder(cmd);
            Process process = pb.start();
            process.waitFor();
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line, deviceId, deviceName;
            while ((line = br.readLine()) != null) {
                if (!line.toLowerCase().trim().contains("list of devices")) {
                    if (line.toLowerCase().trim().contains("device")) {
                        deviceId = line.split("\\s")[0];
                        deviceIds.add(deviceId);
                    }
                }
            }
            br.close();

            for (String id : deviceIds) {
                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.manufacturer" };
                pb.command(cmd);
                process = pb.start();
                process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                deviceName = br.readLine();
                br.close();

                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.model" };
                pb.command(cmd);
                process = pb.start();
                process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                deviceName += " " + br.readLine();
                br.close();

                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.build.version.release" };
                pb.command(cmd);
                process = pb.start();
                process.waitFor();
                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                deviceName += " " + br.readLine();
                br.close();

                deviceName += " (Android)";

                devicesList.put(id, deviceName);
            }
        }
    }
}
