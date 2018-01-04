package com.kms.katalon.composer.integration.kobiton.testsuite;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.integration.kobiton.constants.KobitonImageConstants;
import com.kms.katalon.composer.integration.kobiton.dialog.KobitonDeviceDialog;
import com.kms.katalon.composer.mobile.execution.testsuite.MobileTestExecutionDriverEntry;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.integration.kobiton.configuration.KobitonRunConfiguration;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;

public class KobitonTestExecutionDriverEntry extends MobileTestExecutionDriverEntry {

    public KobitonTestExecutionDriverEntry(String groupName) {
        super(WebUIDriverType.KOBITON_WEB_DRIVER, groupName, KobitonImageConstants.IMG_16_KOBITON_URL.toString());
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(Composite parent) {
        return new AbstractDialogCellEditor(parent) {

            @Override
            protected void updateContents(Object value) {
                super.updateContents(getDeviceName(value));
            }

            @SuppressWarnings("unchecked")
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                KobitonDeviceDialog dialog = new KobitonDeviceDialog(getParentShell(),
                        getDevice((Map<String, String>) getValue()));
                if (dialog.open() != KobitonDeviceDialog.OK) {
                    return null;
                }
                Map<String, String> runConfigurationData = new HashMap<>();
                runConfigurationData.put(KobitonRunConfiguration.KOBITON_DEVICE_PROPERTY,
                        JsonUtil.toJson(dialog.getSelectedDevice()));
                return runConfigurationData;
            }
        };
    }
    
    @Override
    public Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData) {
        KobitonDeviceDialog dialog = new KobitonDeviceDialog(shell,
                getDevice(runConfigurationData));
        if (dialog.open() != KobitonDeviceDialog.OK) {
            return runConfigurationData;
        }
        Map<String, String> newValue = new HashMap<>();
        newValue.put(KobitonRunConfiguration.KOBITON_DEVICE_PROPERTY,
                JsonUtil.toJson(dialog.getSelectedDevice()));
        return newValue;
    }

    private KobitonDevice getDevice(Map<String, String> runConfigurationData) {
        if (runConfigurationData == null) {
            return null;
        }
        String kobitonDeviceJson = runConfigurationData.get(KobitonRunConfiguration.KOBITON_DEVICE_PROPERTY);
        if (StringUtils.isEmpty(kobitonDeviceJson)) {
            return null;
        }
        return JsonUtil.fromJson(kobitonDeviceJson, KobitonDevice.class);
    }


    private String getDeviceName(Object value) {
        @SuppressWarnings("unchecked")
        KobitonDevice device = getDevice((Map<String, String>) value);
        String deviceName = device != null ? device.getDisplayString() : StringUtils.EMPTY;
        return deviceName;
    }
    
    
    @Override
    public String displayRunConfigurationData(Map<String, String> runConfigurationData) {
        return getDeviceName(runConfigurationData);
    }
}
