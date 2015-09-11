package com.kms.katalon.composer.mobile.dialog;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.kms.katalon.composer.components.log.LoggerSingleton;
import com.kms.katalon.composer.mobile.constants.StringConstants;

public class DeviceSelectionDialog extends TitleAreaDialog {

	private String deviceName;
	private Combo cbbDevices;
	private Composite container;
	private String platform;

	private Map<String, String> devicesList;

	public DeviceSelectionDialog(Shell parentShell, String platform) {
		super(parentShell);
		this.platform = platform;
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
		if (StringConstants.OS_ANDROID.equalsIgnoreCase(platform)) {
            getAndroidDevices();
        } else if (StringConstants.OS_IOS.equalsIgnoreCase(platform)) {
            getIosDevices();
        }

		Label theLabel = new Label(container, SWT.NONE);
		theLabel.setText(StringConstants.DIA_DEVICE_NAME);

		cbbDevices = new Combo(container, SWT.READ_ONLY);
		cbbDevices.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		cbbDevices.setItems(devicesList.values().toArray(new String[] {}));
		if (devicesList.size() > 0) {
			cbbDevices.select(0);
		}
		cbbDevices.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateStatus();
			}
		});

		return area;
	}

	private void updateStatus() {
		if (cbbDevices.getSelectionIndex() < 0) {
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
		setTitle(platform + " Devices");
		setMessage(StringConstants.DIA_SELECT_DEVICE_NAME_MSG, IMessageProvider.INFORMATION);
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 200);
	}

	@Override
	protected void okPressed() {
	    if (StringConstants.OS_ANDROID.equalsIgnoreCase(platform)) {
	        deviceName = cbbDevices.getText();
        } else if (StringConstants.OS_IOS.equalsIgnoreCase(platform)) {
            String selectedDevice = cbbDevices.getText();
            for (Entry<String, String> device : devicesList.entrySet()) {
                if (device.getValue().equals(selectedDevice)) {
                    deviceName = device.getKey();
                }
            }
        }
		super.okPressed();
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	private void getIosDevices() {
		devicesList.clear();
		if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			List<String> deviceIds = new ArrayList<>();
			String[] cmd = { "idevice_id", "-l" };
			ProcessBuilder pb = new ProcessBuilder(cmd);
			
			Process p = null;
            try {
                p = pb.start();
                p.waitFor();
            } catch (IOException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
            if (p == null) {
                return;
            }
			String line = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			try {
                while ((line = br.readLine()) != null) {
                	deviceIds.add(line);
                }
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }

			for (String deviceId : deviceIds) {
				cmd = new String[] { "ideviceinfo", "-u", deviceId };
				pb.command(cmd);
				p = null;
				try {
	                p = pb.start();
	                p.waitFor();
	            } catch (IOException | InterruptedException e) {
	                LoggerSingleton.logError(e);
	            }
				if (p == null) {
				    continue;
				}
				br = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String deviceInfo = "";
				try {
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
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }

				devicesList.put(deviceId, deviceInfo);
			}
		}
	}

	private void getAndroidDevices() {
		devicesList.clear();
		String adbPath = System.getenv("ANDROID_HOME");
		if (adbPath != null) {
			List<String> deviceIds = new ArrayList<>();
			adbPath += File.separator + "platform-tools" + File.separator + "adb";
			String[] cmd = new String[] { adbPath, "devices" };
			ProcessBuilder pb = new ProcessBuilder(cmd);
			Process process = null;
            try {
                process = pb.start();
                process.waitFor();
            } catch (IOException | InterruptedException e) {
                LoggerSingleton.logError(e);
            }
            if (process == null) {
                return;
            }
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line, deviceId, deviceName;
			try {
                while ((line = br.readLine()) != null) {
                	if (!line.toLowerCase().trim().contains("list of devices")) {
                		if (line.toLowerCase().trim().contains("device")) {
                			deviceId = line.split("\\s")[0];
                			deviceIds.add(deviceId);
                		}
                	}
                }
                br.close();
            } catch (IOException e) {
                LoggerSingleton.logError(e);
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    LoggerSingleton.logError(e);
                }
            }

			for (String id : deviceIds) {
	            try {
	                cmd = new String[] { adbPath, "-s", id, "shell", "getprop", "ro.product.manufacturer" };
	                pb.command(cmd);
	                process = pb.start();
	                process.waitFor();

	                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    deviceName = br.readLine();
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

                    devicesList.put(id, deviceName);
	            } catch (IOException | InterruptedException e) {
	                LoggerSingleton.logError(e);
	            } finally {
	                if (br == null) {
	                    continue;
	                }
                    try {
                        br.close();
                    } catch (IOException e) {
                        LoggerSingleton.logError(e);
                    }
                }
			}
		}
	}
}