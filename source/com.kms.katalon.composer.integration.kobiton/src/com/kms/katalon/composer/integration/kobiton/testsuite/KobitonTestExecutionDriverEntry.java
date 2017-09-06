package com.kms.katalon.composer.integration.kobiton.testsuite;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.kms.katalon.composer.integration.kobiton.constants.KobitonImageConstants;
import com.kms.katalon.composer.integration.kobiton.dialog.KobitonDeviceDialog;
import com.kms.katalon.composer.mobile.execution.testsuite.MobileTestExecutionDriverEntry;
import com.kms.katalon.core.util.internal.JsonUtil;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.entity.testsuite.RunConfigurationDescription;
import com.kms.katalon.integration.kobiton.configuration.KobitonRunConfiguration;
import com.kms.katalon.integration.kobiton.entity.KobitonDevice;

public class KobitonTestExecutionDriverEntry extends MobileTestExecutionDriverEntry {

    public KobitonTestExecutionDriverEntry(String groupName) {
        super(WebUIDriverType.KOBITON_WEB_DRIVER, groupName, KobitonImageConstants.IMG_16_KOBITON_URL.toString());
    }

    @Override
    public RunConfigurationDescription toConfigurationEntity() {
        return super.toConfigurationEntity();
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(ColumnViewer parent) {
        return new DialogCellEditor((Composite) parent.getControl()) {

            @Override
            protected void updateContents(Object value) {
                super.updateContents(getDeviceName(value));
            }

            @SuppressWarnings("unchecked")
            @Override
            protected Object openDialogBox(Control cellEditorWindow) {
                KobitonDeviceDialog dialog = new KobitonDeviceDialog(cellEditorWindow.getShell(),
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
