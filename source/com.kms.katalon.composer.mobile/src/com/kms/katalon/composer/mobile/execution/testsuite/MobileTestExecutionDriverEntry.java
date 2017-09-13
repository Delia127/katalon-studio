package com.kms.katalon.composer.mobile.execution.testsuite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.execution.collection.provider.TestExecutionDriverEntry;
import com.kms.katalon.composer.execution.util.MapUtil;
import com.kms.katalon.composer.mobile.dialog.DeviceSelectionDialog;
import com.kms.katalon.core.driver.DriverType;
import com.kms.katalon.core.mobile.driver.MobileDriverType;
import com.kms.katalon.execution.mobile.configuration.contributor.MobileRunConfigurationContributor;
import com.kms.katalon.execution.mobile.device.MobileDeviceInfo;

public abstract class MobileTestExecutionDriverEntry extends TestExecutionDriverEntry {
    public MobileTestExecutionDriverEntry(final DriverType driverType, final String groupName, final String imageUrl) {
        super(driverType, groupName, imageUrl);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(Composite parent) {
        return new DialogCellEditor(parent) {
            @Override
            protected void updateContents(Object value) {
                Map<String, String> newValueMap = MapUtil.convertObjectToStringMap(value);
                if (newValueMap == null) {
                    super.updateContents(value);
                    return;
                }
                super.updateContents(MapUtil.buildStringForMap(newValueMap));
            }

            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                Map<String, String> newValueMap = MapUtil.convertObjectToStringMap(getValue());
                return changeRunConfigurationData(cellEditorWindow.getShell(), newValueMap);
            }
        };
    }

    @Override
    public Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData) {
        DeviceSelectionDialog deviceSelectionDialog = new DeviceSelectionDialog(shell, (MobileDriverType) driverType);
        if (deviceSelectionDialog.open() != Window.OK) {
            return runConfigurationData;
        }

        Map<String, String> newValueMap = new HashMap<>(runConfigurationData);
        MobileDeviceInfo device = deviceSelectionDialog.getDevice();
        newValueMap.put(MobileRunConfigurationContributor.DEVICE_NAME_CONFIGURATION_KEY, device.getDeviceName());
        newValueMap.put(MobileRunConfigurationContributor.DEVICE_ID_CONFIGURATION_KEY, device.getDeviceId());
        return newValueMap;
    }
    
    @Override
    public boolean requiresExtraConfiguration() {
        return true;
    }
}
