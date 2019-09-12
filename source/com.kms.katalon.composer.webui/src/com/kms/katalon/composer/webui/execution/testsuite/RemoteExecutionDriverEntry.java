package com.kms.katalon.composer.webui.execution.testsuite;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.kms.katalon.composer.components.dialogs.AbstractDialogCellEditor;
import com.kms.katalon.composer.execution.collection.provider.TestExecutionDriverEntry;
import com.kms.katalon.composer.execution.util.MapUtil;
import com.kms.katalon.composer.webui.component.dialogs.RemoteExecutionInputDialog;
import com.kms.katalon.composer.webui.constants.ImageConstants;
import com.kms.katalon.core.webui.driver.WebUIDriverType;
import com.kms.katalon.execution.webui.configuration.contributor.RemoteWebRunConfigurationContributor;
import com.kms.katalon.execution.webui.driver.RemoteWebDriverConnector.RemoteWebDriverConnectorType;

public class RemoteExecutionDriverEntry extends TestExecutionDriverEntry {
    protected RemoteExecutionDriverEntry(String groupName) {
        super(WebUIDriverType.REMOTE_WEB_DRIVER, groupName, ImageConstants.IMG_URL_16_REMOTE_WEB);
    }

    @Override
    public CellEditor getRunConfigurationDataCellEditor(Composite parent) {
        return new AbstractDialogCellEditor(parent) {
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
                return changeRunConfigurationData(getParentShell(), newValueMap);
            }
        };
    }

    @Override
    public Map<String, String> changeRunConfigurationData(Shell shell, Map<String, String> runConfigurationData) {
        String remoteWebDriverUrl = runConfigurationData
                .get(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_KEY);
        String remoteWebDriverTypeName = runConfigurationData
                .get(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_TYPE_KEY);
        Map<String, String> newValueMap = new HashMap<>();
        newValueMap.put(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_KEY, remoteWebDriverUrl);
        newValueMap.put(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_TYPE_KEY, remoteWebDriverTypeName);
        RemoteExecutionInputDialog dialog = new RemoteExecutionInputDialog(shell, remoteWebDriverUrl,
                remoteWebDriverTypeName != null ? RemoteWebDriverConnectorType.valueOf(remoteWebDriverTypeName) : null);
        int returnValue = dialog.open();
        if (returnValue == Dialog.OK) {
            newValueMap.put(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_KEY, dialog.getRemoteServerUrl());
            newValueMap.put(RemoteWebRunConfigurationContributor.REMOTE_CONFIGURATION_TYPE_KEY,
                    dialog.getRemoveDriverType().toString());
        }
        return newValueMap;
    }

    @Override
    public boolean requiresExtraConfiguration() {
        return false;
    }
}
